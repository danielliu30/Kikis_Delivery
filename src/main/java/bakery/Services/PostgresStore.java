package bakery.Services;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import bakery.Models.BakedGoods;
import bakery.Models.PurchasedItem;
import bakery.Models.SingleCustomer;
import bakery.Models.entity.BakedGoodRecord;
import bakery.Models.entity.CustomerOrderRecord;
import bakery.Models.entity.CustomerRecord;
import bakery.Models.entity.StoreFrontRecord;
import bakery.Models.entity.ValidationTokenRecord;
import bakery.Repository.BakedGoodRepository;
import bakery.Repository.CustomerOrderRepository;
import bakery.Repository.CustomerRepository;
import bakery.Repository.StoreFrontRepository;
import bakery.Repository.ValidationTokenRepository;

/**
 * Reads and writes customers, inventory and storefront totals in PostgreSQL.
 *
 * @author barney
 */
@Service
class PostgresStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostgresStore.class);
	private static final Gson GSON = new Gson();
	private static final String BASE_URL = "http://localhost:8080/store/";
	private static final String TOTAL_REVENUE = "TotalRevenue";
	private static final String EMAIL_KEY = "email";

	private final CustomerRepository customers;
	private final BakedGoodRepository bakedGoods;
	private final ValidationTokenRepository validationTokens;
	private final CustomerOrderRepository customerOrders;
	private final StoreFrontRepository storeFront;
	private final PasswordSecurity secureDb;

	@Autowired
	PostgresStore(CustomerRepository customers, BakedGoodRepository bakedGoods,
			ValidationTokenRepository validationTokens, CustomerOrderRepository customerOrders,
			StoreFrontRepository storeFront, PasswordSecurity secureDb) {
		this.customers = customers;
		this.bakedGoods = bakedGoods;
		this.validationTokens = validationTokens;
		this.customerOrders = customerOrders;
		this.storeFront = storeFront;
		this.secureDb = secureDb;
	}

	/**
	 * @return every category that currently has at least one item on the shelf
	 */
	List<String> getCategories() {
		return bakedGoods.findDistinctCategories();
	}

	List<Map<String, String>> getBakedGoodCategoryList(String category) {
		List<Map<String, String>> result = new LinkedList<>();
		if (category == null) {
			return result;
		}
		for (BakedGoodRecord item : bakedGoods.findByBakedItem(category)) {
			result.add(describe(item));
		}
		return result;
	}

	void addBakedItem(BakedGoods item) {
		BakedGoodRecord record = new BakedGoodRecord();
		LocalDateTime madeAt = LocalDateTime.now();

		record.setBakedItem(item.BakedItem);
		record.setItemVariation(madeAt.toString());
		record.setExpirationTime(madeAt.plusDays(item.getExpirationTime()));
		record.setSize(item.size);
		record.setShape(item.shape);
		record.setCount(item.count);
		record.setFlavor(item.flavor);
		record.setLayers(item.layers);
		record.setCalories(item.calories);
		record.setToppings(item.toppings);
		record.setFillings(item.fillings);
		record.setVegan(item.vegan);
		record.setGlutenFree(item.glutenFree);
		record.setCost(item.cost);

		bakedGoods.save(record);
	}

	List<Map<String, String>> getCustomerList() {
		List<Map<String, String>> result = new LinkedList<>();
		for (CustomerRecord customer : customers.findAll()) {
			Map<String, String> entry = new HashMap<>();
			entry.put(EMAIL_KEY, customer.getEmail());
			entry.put("name", customer.getName());
			entry.put("member", customer.getMember());
			entry.put("admin", customer.getAdmin());
			entry.put("updated", String.valueOf(customer.getUpdated()));
			entry.put("delete", BASE_URL + "deleteCustomer/" + EMAIL_KEY + "/" + customer.getEmail());
			result.add(entry);
		}
		return result;
	}

	// @Transactional only applies to public methods on the Spring proxy
	@Transactional
	public String deleteBakedItem(PurchasedItem purchased) {
		if (purchased.orderList == null) {
			return "Nothing to delete";
		}
		if (purchased.userName == null || !customers.existsById(purchased.userName)) {
			LOGGER.error("Unable to complete purchase. Customer {} does not exist", purchased.userName);
			return "Customer does not exist";
		}
		for (BakedGoods item : purchased.orderList) {
			Optional<BakedGoodRecord> stocked = bakedGoods.findByBakedItemAndItemVariation(item.BakedItem,
					item.ItemVariation);
			if (!stocked.isPresent()) {
				LOGGER.error("Unable to delete item. {}/{} is not on the shelf", item.BakedItem, item.ItemVariation);
				continue;
			}
			BakedGoodRecord record = stocked.get();
			bakedGoods.delete(record);
			storeCustomerHistory(record, purchased.userName);
		}
		return "Successfully deleted";
	}

	private void storeCustomerHistory(BakedGoodRecord purchasedItem, String email) {
		CustomerOrderRecord order = new CustomerOrderRecord();
		order.setCustomerEmail(email);
		order.setItem(GSON.toJson(describe(purchasedItem)));
		customerOrders.save(order);
	}

	String deleteCustomer(String partitionKey, String keyValue) {
		if (!EMAIL_KEY.equals(partitionKey)) {
			LOGGER.error("Unable to delete customer. Customers are keyed by {}, not {}", EMAIL_KEY, partitionKey);
			return "Unsupported key";
		}
		if (!customers.existsById(keyValue)) {
			LOGGER.error("Unable to delete customer. {} does not exist", keyValue);
			return "Customer does not exist";
		}
		customers.deleteById(keyValue);
		return "Successfully deleted";
	}

	/**
	 * Stores the token together with the signup it authorises, so a restart
	 * between signup and verification does not lose the pending account.
	 */
	void add24HrValidationToken(String token, SingleCustomer customer) {
		ValidationTokenRecord record = new ValidationTokenRecord();
		record.setTokenId(token);
		record.setEmail(customer.getEmail());
		record.setPendingCustomer(GSON.toJson(customer));
		record.setExpiration(LocalDateTime.now().plusHours(24));
		validationTokens.save(record);
	}

	@Transactional
	public Boolean verifyToken(String token) {
		Optional<ValidationTokenRecord> stored = validationTokens.findById(token);
		if (!stored.isPresent()) {
			return false;
		}
		ValidationTokenRecord record = stored.get();
		validationTokens.delete(record);

		if (record.getExpiration().isBefore(LocalDateTime.now())) {
			return false;
		}
		addCustomerMember(GSON.fromJson(record.getPendingCustomer(), SingleCustomer.class));
		return true;
	}

	private void addCustomerMember(SingleCustomer customer) {
		CustomerRecord record = new CustomerRecord();
		record.setEmail(customer.getEmail());
		record.setName(customer.getName());
		record.setMember(customer.getMember());
		record.setAdmin(customer.getAdmin());
		try {
			record.setPassword(secureDb.encryptPassWord(customer.getPassWord()));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// Rolls back the token deletion so the signup can still be verified
			throw new IllegalStateException("Password could not be hashed", e);
		}
		customers.save(record);
	}

	boolean checkIfUserExist(SingleCustomer customer) {
		return customer.getEmail() != null && customers.existsById(customer.getEmail());
	}

	@Transactional
	public void updateTotalRevenue(double amount) {
		if (storeFront.increment(TOTAL_REVENUE, BigDecimal.valueOf(amount)) == 0) {
			StoreFrontRecord fresh = new StoreFrontRecord();
			fresh.setId(TOTAL_REVENUE);
			fresh.setTotalMoneyMade(BigDecimal.valueOf(amount));
			storeFront.save(fresh);
		}
	}

	boolean validateLogIn(SingleCustomer customer) throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (customer.getEmail() == null || customer.getPassWord() == null) {
			return false;
		}
		Optional<CustomerRecord> stored = customers.findById(customer.getEmail());
		if (!stored.isPresent()) {
			return false;
		}
		return secureDb.validiatePassword(customer.getPassWord(), stored.get().getPassword());
	}

	private Map<String, String> describe(BakedGoodRecord item) {
		Map<String, String> attributes = new HashMap<>();
		attributes.put("BakedItem", item.getBakedItem());
		attributes.put("ItemVariation", item.getItemVariation());
		attributes.put("ExpirationTime", String.valueOf(item.getExpirationTime()));
		attributes.put("size", item.getSize());
		attributes.put("shape", item.getShape());
		attributes.put("count", item.getCount());
		attributes.put("flavor", item.getFlavor());
		attributes.put("layers", item.getLayers());
		attributes.put("calories", item.getCalories());
		attributes.put("toppings", item.getToppings());
		attributes.put("fillings", item.getFillings());
		attributes.put("vegan", item.getVegan());
		attributes.put("glutenFree", item.getGlutenFree());
		attributes.put("cost", item.getCost());
		attributes.values().removeIf(value -> value == null || "null".equals(value));
		attributes.put("Purchase", BASE_URL + "purchaseItem/" + item.getBakedItem() + "/" + item.getItemVariation());
		return attributes;
	}
}
