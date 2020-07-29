package bakery.Services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import bakery.Models.BakedItem;
import bakery.Models.SingleCustomer;

@Service
public class BakedFormation {


	private final DynamoDbConnection dbConnection;
	private final S3Connection s3Connection;
	private static final Gson gson = new Gson();
	private static Set<String> categorySet;
    private final String baseURL = "http://localhost:8080/store/";
	//needed to change privacy from private due to runtime error with @cacheable
	@Autowired
    BakedFormation(DynamoDbConnection dbConnection, S3Connection s3Connection){
        this.dbConnection = dbConnection;
        this.s3Connection = s3Connection;
    }

    public String getCategories() {
    	List<Link> linkSet = new LinkedList<Link>();
    	Map<String,List<String>> reformed = this.getMenu();
    	reformed.get("BakedItem").forEach(item->{
    		linkSet.add(new Link(item,baseURL+item));
    	});
    	linkSet.add(new Link("Customer", baseURL+"customerList"));
		//look into using links instead of link
		return gson.toJson(linkSet);
    }
    public String getAvailableBakedItems(String category){
    	Map<String, Object> reformed = new HashMap<String, Object>();
        reformed.put("Home", new Link("home",baseURL));
        reformed.put(category, dbConnection.getBakedGoodCategoryList(category));
        return gson.toJson(reformed);
         
    }

    public void addAvailableBakedItems(BakedItem item) throws JsonProcessingException{
        dbConnection.addAvailableBakedGoods(item);;
    }

    public void uploadFile(){
        s3Connection.uploadItem();
    }

    //caching for google chrome, since it doesn't honor default http cache
    @Cacheable("allCategories")
    private Map<String,List<String>> getMenu(){
        return s3Connection.retrieveCategoryList();
    }
    
    public void addCustomer(SingleCustomer customer) throws JsonProcessingException{
    	//validate email
    	dbConnection.addCustomerMember(customer);
    }
    
    public String getAllCustomers() {    	
    	return gson.toJson(dbConnection.getCustomerList());
    }
}
