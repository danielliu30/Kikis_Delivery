package bakery.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import bakery.Models.BakedItem;
import bakery.Models.PurchasedItem;
import bakery.Models.SingleCustomer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This is creates a connection between the applicationa and aws DynamoDB it
 * allows the read and write of data for customers and store items
 * 
 * @author barney
 *
 */
@Service
class DynamoDbConnection {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbConnection.class);
	private static final DynamoDbClient client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
	// private static final DynamoDbEnhancedClient enahancedClient =
	// DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
	private static final Gson gson = new Gson();
	private static HashMap<String, SingleCustomer> uncheckedUsers = new HashMap<>();
	private static HashMap<String, AttributeValue> attribute = new HashMap<>();

	private enum ItemKeys {
		Id, Component, AvailableGoods, BakedItem, Variation
	}

	private enum Tables {
		BakedGoods, StoreFront, Customers, ValidationTokens
	}

	private enum Keys {
		BakedItem, ItemVariation, TokenId, email
	}

	private DynamoSecurity secureDb;

	@Autowired
	private DynamoDbConnection(DynamoSecurity secureDb) {
		this.secureDb = secureDb;
	}

	/**
	 * 
	 * @param category
	 * @return
	 */
	// may only be used for getting a single item. Will want to batch fetch when
	// getting larger list
	List<Map<String, String>> getBakedGoodCategoryList(String category) {
		List<Map<String, String>> result = new LinkedList<>();
		ScanIterable response = null;
		StringBuilder partition = new StringBuilder();
		StringBuilder secondary = new StringBuilder();
		ScanRequest request = ScanRequest.builder().tableName(Tables.BakedGoods.name())
				.filterExpression("BakedItem = :bakedItem").expressionAttributeValues(
						Collections.singletonMap(":bakedItem", AttributeValue.builder().s(category).build()))
				.build();

		try {
			response = client.scanPaginator(request);
		} catch (Exception e) {
			LOGGER.error("Unable to find category. Category may not exist or request is invalid", e);
		}
		for (Map<String, AttributeValue> item : response.items()) {
			Map<String, String> tempMap = new HashMap<>();
			for (String indKey : item.keySet()) {
				// String str = item.get(indKey).toString();
				// String temp = str.toString().substring(str.indexOf("S=")+2, str.indexOf(",
				// "));
				if (indKey.equals(Keys.BakedItem.name())) {
					partition.append(item.get(indKey).s() + "/");
				} else if (indKey.equals(Keys.ItemVariation.name())) {
					secondary.append(item.get(indKey).s());
				}
				tempMap.put(indKey, item.get(indKey).s());
			}
			tempMap.put("Purchase",
					new Link("http://localhost:8080/store/purchaseItem/" + partition.append(secondary)).getHref());
			partition.setLength(0);
			secondary.setLength(0);
			result.add(tempMap);
		}
		return result;
	}

	// want to revise how we actually build the item's attribute
	void addAvailableBakedGoods(BakedItem item) throws JsonProcessingException {
		ObjectMapper obj = new ObjectMapper();
		Map<String, String> revisedItem = gson.fromJson(obj.writeValueAsString(item), Map.class);
		LocalDateTime timeMade = LocalDateTime.now();
		LocalDateTime expired = timeMade.plusDays(item.getExpirationTime());

		attribute.clear();
		attribute.put(ItemKeys.BakedItem.name(), AttributeValue.builder().s(revisedItem.get("category")).build());
		attribute.put("ItemVariation", AttributeValue.builder().s(timeMade.toString()).build());
		attribute.put("ExpirationTime", AttributeValue.builder().s(expired.toString()).build());
		revisedItem.remove("category");
		revisedItem.remove("expirationTime");
		for (Map.Entry<String, String> pair : revisedItem.entrySet()) {
			attribute.put(pair.getKey(), AttributeValue.builder().s(pair.getValue()).build());
		}

		PutItemRequest request = PutItemRequest.builder().tableName(Tables.BakedGoods.name()).item(attribute).build();

		try {
			client.putItem(request);
		} catch (DynamoDbException e) {
			LOGGER.error("Unable to add item. Item schema may be incorrect", e);
		}
	}

	private String addCustomerMember(SingleCustomer customer) throws JsonProcessingException {
		ObjectMapper obj = new ObjectMapper();
		Map<String, String> revisedItem = gson.fromJson(obj.writeValueAsString(customer), Map.class);
		String encrypt = "";
		//need to rebuild this for loop is  fucking trolling
		for (Map.Entry<String, String> pair : revisedItem.entrySet()) {
			if (pair.getKey().equals("password")) {
				try {
					encrypt = secureDb.encryptPassWord(pair.getValue());
					attribute.put(pair.getKey(), AttributeValue.builder().s(encrypt).build());
				} catch (Exception e) {
					// unable to encrypt
				}
			} else {
				attribute.put(pair.getKey(), AttributeValue.builder().s(pair.getValue()).build());
			}

		}
		attribute.put("orders", AttributeValue.builder().m(new HashMap<String, AttributeValue>()).build());
		PutItemRequest request = PutItemRequest.builder().tableName(Tables.Customers.name()).item(attribute).build();

		try {
			client.putItem(request);
		} catch (DynamoDbException e) {
			LOGGER.error("Unable to add customer", e);
		}
		return "Successfully added customer";
	}

	List<Map<String, String>> getCustomerList() {
		ScanResponse response;
		List<Map<String, String>> result = new LinkedList<>();
		try {
			ScanRequest request = ScanRequest.builder().tableName(Tables.Customers.name()).build();
			response = client.scan(request);

			for (Map<String, AttributeValue> item : response.items()) {
				Map<String, String> tempMap = new HashMap<String, String>();
				for (String indKey : item.keySet()) {

					String str = item.get(indKey).s();

					if (indKey.equals("email")) {
						tempMap.put("delete",
								new Link("http://localhost:8080/store/deleteCustomer/" + indKey + "/" + str).getHref());
					}
					tempMap.put(indKey, str);
				}
				result.add(tempMap);
			}
		} catch (DynamoDbException e) {
			LOGGER.error("Unable to get customer List. Request is invalid", e);
		}
		return result;
	}

	String deleteBakedItem(PurchasedItem purchased) {

		for (BakedItem item : purchased.orderList) {
			attribute.clear();
			attribute.put(Keys.BakedItem.name(), AttributeValue.builder().s(item.BakedItem).build());
			attribute.put(Keys.ItemVariation.name(), AttributeValue.builder().s(item.ItemVariation).build());
			DeleteItemRequest request = DeleteItemRequest.builder().tableName(Tables.BakedGoods.name()).key(attribute)
					.returnValues(ReturnValue.ALL_OLD).build();

			try {
				var response = client.deleteItem(request);
				if (response.hasAttributes()) {
					var purchasedItem = response.attributes();
					storeCustomerHistory(purchasedItem, purchased);
				}
			} catch (DynamoDbException e) {
				LOGGER.error("Unable to delete item. May be due to incorrect schema", e);
			}
		}

		return "Successfully deleted";
	}

	private void storeCustomerHistory(Map<String, AttributeValue> purchasedItem, PurchasedItem item) {
		attribute.clear();
		attribute.put(Keys.email.name(), AttributeValue.builder().s(item.userName).build());

		Map<String, String> hierarchy = new HashMap<>();
		Map<String, AttributeValue> updateOrder = new HashMap<>();

		updateOrder.put(":v", AttributeValue.builder().m(purchasedItem).build());
		hierarchy.put("#k", "orders");
		hierarchy.put("#l", LocalDateTime.now().toString());

		UpdateItemRequest request = UpdateItemRequest.builder().tableName(Tables.Customers.name()).key(attribute)
				.expressionAttributeNames(hierarchy).expressionAttributeValues(updateOrder)
				.updateExpression("set #k.#l = :v").build();
		try {
			client.updateItem(request);
		} catch (DynamoDbException e) {
			LOGGER.error("Unable to update history. May be incorrect schema or invalid customer", e);
		}
	}

	String deleteCustomer(String partitionKey, String keyValue) {
		attribute.clear();
		attribute.put(partitionKey, AttributeValue.builder().s(keyValue).build());

		DeleteItemRequest request = DeleteItemRequest.builder().tableName(Tables.Customers.name()).key(attribute)
				.build();

		try {
			client.deleteItem(request);
		} catch (DynamoDbException e) {
			// do some logging
			LOGGER.error("Unable to delete customer. Customer may not exist or Request is invalid", e);;
		}
		return "Successfully deleted";
	}

	void add24HrValidationToken(String token, SingleCustomer customer) {
		uncheckedUsers.put(token, customer);
		attribute.clear();
		attribute.put(Keys.TokenId.name(), AttributeValue.builder().s(token).build());
		attribute.put("Expiration", AttributeValue.builder().s(LocalDateTime.now().plusHours(24).toString()).build());

		PutItemRequest request = PutItemRequest.builder().tableName(Tables.ValidationTokens.name()).item(attribute)
				.build();

		try {
			client.putItem(request);
		} catch (DynamoDbException e) {
			LOGGER.error("Failed to add valid token. May be a duplicate", e);
		}
	}

	Boolean verifyToken(String token) throws JsonProcessingException {
		attribute.clear();
		attribute.put(Keys.TokenId.name(), AttributeValue.builder().s(token).build());
		GetItemResponse response = null;
		GetItemRequest request = GetItemRequest.builder().key(attribute).tableName(Tables.ValidationTokens.name())
				.build();
		try {
			response = client.getItem(request);
		} catch (Exception e) {
			LOGGER.error("Failed to verify token. Invalid request", e);
		}
		if (response.item().isEmpty()) {
			return false;
		} else {
			if (LocalDateTime.parse(response.item().get("Expiration").s()).isAfter(LocalDateTime.now())) {
				addCustomerMember(uncheckedUsers.get(token));
				return true;
			}
			return false;
		}
	}

	boolean checkIfUserExist(SingleCustomer customer) {
		attribute.clear();
		attribute.put(Keys.email.name(), AttributeValue.builder().s(customer.getEmail()).build());
		GetItemRequest request = GetItemRequest.builder().key(attribute)
				.tableName(Tables.Customers.name()).build();

		try {
			GetItemResponse response = client.getItem(request);
			if(!response.hasItem()){
				uncheckedUsers.put(customer.getEmail(), customer);
			}
			return response.hasItem();
		} catch (DynamoDbException e) {
			LOGGER.error("Failed to check if user exists. Schema may be incorrect", e);	
		}
		return false;
		// Add purchased good to associated account/user
	}

	boolean validateLogIn(SingleCustomer customer) throws NoSuchAlgorithmException, InvalidKeySpecException {
		attribute.clear();
		attribute.put(Keys.email.name(), AttributeValue.builder().s(customer.getEmail()).build());
		GetItemRequest request = GetItemRequest.builder().key(attribute).tableName(Tables.Customers.name()).build();
		try {

			GetItemResponse response = client.getItem(request);
			if (response.hasItem()) {
				boolean decrypt = secureDb.validiatePassword(customer.getPassWord(),
						response.item().get("password").s());
				return response.hasItem() && response.item().get("email").s().equals(customer.getEmail()) && decrypt;
			} else {
				uncheckedUsers.put(customer.getEmail(), customer);
			}

		} catch (DynamoDbException e) {
			
			LOGGER.error("Unable to validate. User may not exist or schema is incorrect", e);
		}
		return false;
		// Add purchased good to associated account/user
	}
}
