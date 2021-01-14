package bakery.Services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import bakery.Models.BakedItem;
import bakery.Models.PurchasedItem;
import bakery.Models.SingleCustomer;

/**
 * An abstraction layer between aws services and all REST controllers
 * 
 * @author barney
 *
 */
@Service
public class BakedFormation {

    private final DynamoDbConnection dbConnection;
    private final S3Connection s3Connection;
    private final EmailConnection emailConnection;
    private static final Gson gson = new Gson();

    private final String baseURL = "http://localhost:8080/store/";

    // needed to change privacy from private due to runtime error with @cacheable
    @Autowired
    BakedFormation(DynamoDbConnection dbConnection, S3Connection s3Connection, EmailConnection emailConnection) {
        this.dbConnection = dbConnection;
        this.s3Connection = s3Connection;
        this.emailConnection = emailConnection;
    }

    /**
     * Makes a call to retrieve the menu list and parses the items to generate a
     * link for all categories.
     * 
     * @return a JSON string with all links to categories and customers
     */
    public String getCategories() {
        List<Link> linkSet = new LinkedList<Link>();
        Map<String, List<String>> reformed = this.getMenu();
        reformed.get("BakedItem").forEach(item -> {
            linkSet.add(new Link(item, baseURL + item));
        });
        linkSet.add(new Link("Customer", baseURL + "customerList"));
        // look into using links instead of link
        return gson.toJson(linkSet);
    }

    /**
     * Makes a call to retrieve of all baked items in a specified category and
     * generates links for navigation back to main page
     * 
     * @param category a string that specifies the type of baked good
     * @return a JSON string of all items in a specified category
     */
    public String getAvailableBakedItems(String category) {
        Map<String, Object> reformed = new HashMap<String, Object>();
        reformed.put("Home", new Link("home", baseURL));
        reformed.put(category, dbConnection.getBakedGoodCategoryList(category));
        return gson.toJson(reformed);

    }

    /**
     * Sends a BakedItem to be processed for storage
     * 
     * @param item a BakedItem that represents a single baked good
     * @throws JsonProcessingException if Object can not be serialized into a JSON
     *                                 string
     */
    public void addAvailableBakedItems(BakedItem item) throws JsonProcessingException {
        dbConnection.addAvailableBakedGoods(item);
    }

    public void uploadFile() {
        s3Connection.uploadItem();
    }

    /**
     * 
     * @return
     */
    // caching for google chrome, since it doesn't honor default http cache
    @Cacheable("allCategories")
    private Map<String, List<String>> getMenu() {
        return s3Connection.retrieveCategoryList();
    }

    public void addCustomer(SingleCustomer customer) throws JsonProcessingException {
        // validate email
        // currently only allowing the validation of users to be added as acustomer
        // dbConnection.addCustomerMember(customer);
    }

    public String getAllCustomers() {
        return gson.toJson(dbConnection.getCustomerList());
    }

    public void deleteCustomer(String partitionKey, String keyValue) {
        dbConnection.deleteCustomer(partitionKey, keyValue);
    }

    public void deleteStoreItem(PurchasedItem item) {
        dbConnection.deleteBakedItem(item);
    }

    public String genreateValidationToken(SingleCustomer customer) {
        String token = UUID.randomUUID().toString();
        dbConnection.add24HrValidationToken(token, customer);
        return token;
    }

    public boolean checkExisitingUser(SingleCustomer customer) {
        return dbConnection.checkIfUserExist(customer);
    }

    public void sendVerificationToken(String token, String email) {
        emailConnection.sendEmailToken(token, email);
    }

    public Boolean checkValidationToken(String token) throws JsonProcessingException {
        return dbConnection.verifyToken(token);
    }

    public Boolean validateLogIn(SingleCustomer customer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return dbConnection.validateLogIn(customer);
    }

    //testing getters
    public DynamoDbConnection getDynamoDbConnection(){
        return dbConnection;
    }

    public EmailConnection getEmailConnection(){
        return emailConnection;
    }
}
