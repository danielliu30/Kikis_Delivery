package bakery.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import bakery.Models.BakedItem;
import bakery.Models.SingleCustomer;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;

import java.time.LocalDateTime;
import java.util.*;

/**
 * This is creates a connection between the applicationa and aws DynamoDB
 * it allows the read and write of data for customers and store items
 * @author barney
 *
 */
@Service
public class DynamoDbConnection {

    private static final DynamoDbClient client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
//    private static final DynamoDbEnhancedClient enahancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    private static final Gson gson = new Gson();

    private static HashMap<String, AttributeValue> attribute = new HashMap<>();
																																																																																																																																																																																																																																																			   private enum ItemKeys{Id, Component, AvailableGoods, BakedItem, Variation}
    private enum Tables{BakedGoods, StoreFront, Customers}
    private enum Keys{BakedItem, ItemVariation}

    private DynamoDbConnection(){
    }

    /**
     * 
     * @param category
     * @return
     */
    //may only be used for getting a single item. Will want to batch fetch when getting larger list
    List<Map<String,String>> getBakedGoodCategoryList(String category){
    	List<Map<String,String>> result = new LinkedList<>();
    	ScanIterable response = null;
    	StringBuilder partition = new StringBuilder();
    	StringBuilder secondary = new StringBuilder();
    	ScanRequest request = ScanRequest.builder()
    			.tableName(Tables.BakedGoods.name())
    			.filterExpression("BakedItem = :bakedItem")
    			.expressionAttributeValues(
    					Collections.singletonMap(":bakedItem", AttributeValue.builder().s(category).build()))
    			.build();
    		
    	try {
    		response = client.scanPaginator(request);	
    	}catch(Exception e) {
    		
    	}
    	for (Map<String, AttributeValue> item: response.items()){
            Map<String, String> tempMap = new HashMap();
            for (String indKey : item.keySet()) {
            	String str = item.get(indKey).toString();
            	String temp = str.toString().substring(str.indexOf("S=")+2, str.indexOf(", "));
            	if(indKey.equals(Keys.BakedItem.name())) {
            		partition.append(temp+"/");
            	}else if(indKey.equals(Keys.ItemVariation.name())) {
            		secondary.append(temp);
            	}
                tempMap.put(indKey, temp);               
            }
            tempMap.put("Purchase", new Link("http://localhost:8080/store/purchaseItem/"+partition.append(secondary)).getHref());
            partition.setLength(0);
            secondary.setLength(0);
            result.add(tempMap);
        }
        return result;
    }

    //want to revise how we actually build the item's attribute
    void addAvailableBakedGoods(BakedItem item) throws JsonProcessingException  {
		ObjectMapper obj = new ObjectMapper();
		Map<String, String> revisedItem = gson.fromJson(obj.writeValueAsString(item), Map.class);
		LocalDateTime timeMade = LocalDateTime.now();
		LocalDateTime expired = timeMade.plusDays(item.getExpirationTime());
		
		attribute.clear();
		attribute.put(ItemKeys.BakedItem.name(), AttributeValue.builder().s(revisedItem.get("category")).build());
		attribute.put("ItemVariation", AttributeValue.builder().s(timeMade.toString()).build());
		attribute.put("ExpirationDate", AttributeValue.builder().s(expired.toString()).build());
		revisedItem.remove("category");
		revisedItem.remove("expirationTime");
		for (Map.Entry<String, String> pair : revisedItem.entrySet()) {

			attribute.put(pair.getKey(), AttributeValue.builder().s(pair.getValue()).build());

		}

				
		PutItemRequest request = PutItemRequest.builder()
		         .tableName(Tables.BakedGoods.name())
		         .item(attribute)
		         .build();

		try {
			client.putItem(request);
		} catch (DynamoDbException e) {

		}


    }

    //need POST req to be json
    String addCustomerMember(SingleCustomer customer) throws JsonProcessingException{
    	ObjectMapper obj = new ObjectMapper();
		Map<String, String> revisedItem = gson.fromJson(obj.writeValueAsString(customer), Map.class);
        attribute.clear();
        
        for (Map.Entry<String, String> pair : revisedItem.entrySet()) {
        	attribute.put(pair.getKey(), AttributeValue.builder().s(pair.getValue()).build());
        }
        //add reward card


        PutItemRequest request = PutItemRequest.builder()
                .tableName(Tables.Customers.name())
                .item(attribute)
                .build();

        try{
            client.putItem(request);
        }catch (DynamoDbException e){
            return e.getMessage();
        }

        return "Successfully added customer";
    }


    List<Map<String,String>> getCustomerList(){
       ScanResponse response;
       List<Map<String,String>> result = new LinkedList<>();
        try{
            ScanRequest request = ScanRequest.builder()
                    .tableName(Tables.Customers.name())
                    .build();
            response = client.scan(request);

            for (Map<String, AttributeValue> item: response.items()){
                Map<String, String> tempMap = new HashMap<String, String>();
                for (String indKey : item.keySet()) {
                	
                    String str = item.get(indKey).toString();
                    String parsed = str.substring(str.indexOf("S=")+2, str.indexOf(", "));
                    if(indKey.equals("email")) {
                    	tempMap.put("delete", new Link("http://localhost:8080/store/deleteCustomer/"+indKey+"/"+parsed).getHref());
                    }
                    tempMap.put(indKey, parsed);
                }
                result.add(tempMap);
            }
        }catch (DynamoDbException e){
            //log some error;
        }
        return result;
    }

    
	String deleteBakedItem(String category, String timeStamp) {
		attribute.clear();
		attribute.put(Keys.BakedItem.name(), AttributeValue.builder().s(category).build());
		attribute.put(Keys.ItemVariation.name(), AttributeValue.builder().s(timeStamp).build());
		DeleteItemRequest request = DeleteItemRequest.builder()
				.tableName(Tables.BakedGoods.name())
				.key(attribute)
				.build();
		
		try {
			client.deleteItem(request);
		}catch(DynamoDbException e) {
			//do some logging
			return "failed";
		}
		
		return "Successfully deleted";
	}

	String deleteCustomer(String partitionKey, String keyValue) {
		attribute.clear();
		attribute.put(partitionKey, AttributeValue.builder().s(keyValue).build());
		
		DeleteItemRequest request = DeleteItemRequest.builder()
				.tableName(Tables.Customers.name())
				.key(attribute)
				.build();
		
		try {
			client.deleteItem(request);
		}catch(DynamoDbException e) {
			//do some logging
			return "failed";
		}
		
		return "Successfully deleted";
	}
//
//    private void makeUpdateRequest(Map<String, AttributeValueUpdate> updatedValues){
//        try{
//            UpdateItemRequest request = UpdateItemRequest.builder()
//                    .tableName(Tables.StoreFront.name())
//                    .key(attribute)
//                    .updateExpression("Baked")
//                    .attributeUpdates(updatedValues)
//                    .build();
//            client.updateItem(request);
//        }catch (ResourceNotFoundException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
//        } catch (DynamoDbException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
//        }
//    }

}
