package bakery.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import bakery.Models.BakedItem;
import bakery.Models.SingleCustomer;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DynamoDbConnection {

    private static final DynamoDbClient client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
//    private static final DynamoDbEnhancedClient enahancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    private static final Gson gson = new Gson();

    private static HashMap<String, AttributeValue> attribute = new HashMap<>();
    private enum ItemKeys{Id, Component, AvailableGoods, BakedItem, Variation}
    private enum Tables{BakedGoods, StoreFront, Customers}

    private DynamoDbConnection(){
    }

    //may only be used for getting a single item. Will want to batch fetch when getting larger list
    String getAvailableBakedGoods(){
        attribute.clear();
        Map<String, AttributeValue> response = new HashMap<>();
        attribute.put(ItemKeys.Component.name(), AttributeValue.builder().s(ItemKeys.AvailableGoods.name()).build());
        Set<String> availableItem = new HashSet<>();
        try{
            GetItemRequest request = GetItemRequest.builder()
                    .tableName(Tables.StoreFront.name())
                    .key(attribute)
                    .projectionExpression("Baked")
                    .build();

            response = client.getItem(request).item();

            response.values().forEach(val->{
                for (AttributeValue item: val.l()) {
                    availableItem.add(item.s());
                }
            });

        }catch (DynamoDbException e){

        }

        return gson.toJson(availableItem);
    }

    void addAvailableBakedGoods(BakedItem item) throws JsonProcessingException  {
		ObjectMapper obj = new ObjectMapper();
		Map<String, String> revisedItem = gson.fromJson(obj.writeValueAsString(item), Map.class);
		attribute.put(ItemKeys.BakedItem.name(), AttributeValue.builder().s(revisedItem.get("category")).build());
		attribute.put("ItemVariation", AttributeValue.builder().s(LocalDateTime.now().toString()).build());
		revisedItem.remove("Category");
		for (Map.Entry<String, String> pair : revisedItem.entrySet()) {
			if(!pair.getKey().equals("category")) {
				attribute.put(pair.getKey(), AttributeValue.builder().s(pair.getValue()).build());
			}
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
    String addCustomerMember(SingleCustomer customer){
        attribute.clear();
        attribute.put(ItemKeys.Id.name().toLowerCase(), AttributeValue.builder().s(customer.getEmail()).build());
        attribute.put("Name", AttributeValue.builder().s(customer.getName()).build());
        attribute.put("Member", AttributeValue.builder().s(customer.getMemberStatus()).build());
        attribute.put("LastUpdated", AttributeValue.builder().s(customer.getLastUpdated()).build());
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


    String getCustomerList(){
       ScanResponse response;
       Set<Map<String,String>> result = new HashSet<>();
        try{
            ScanRequest request = ScanRequest.builder()
                    .tableName(Tables.Customers.name())
                    .build();
            response = client.scan(request);

            for (Map<String, AttributeValue> item: response.items()){
                Map<String, String> tempMap = new HashMap<String, String>();
                for (String indKey : item.keySet()) {
                    String str = indKey.substring(indKey.indexOf('S'), indKey.indexOf('='));
                    tempMap.put(indKey, str);
                }
                result.add(tempMap);
            }
        }catch (DynamoDbException e){
            return e.getMessage();
        }
        return gson.toJson(result);
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
