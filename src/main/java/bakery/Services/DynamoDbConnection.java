package bakery.Services;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

@Service
public class DynamoDbConnection {

    private static final DynamoDbClient client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    private static final DynamoDbEnhancedClient enahancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    private static final Gson gson = new Gson();

    private static HashMap<String, AttributeValue> attribute = new HashMap<>();
    private enum ItemKeys{Component, AvailableGoods, BakedItem, Variation}
    private enum Tables{BakedGoods, StoreFront}

    private final String AVAILABLEITEM = "C:\\Users\\barney\\IdeaProjects\\bakery.bakeshop\\src\\main\\resources\\AvailableItems.json";


    private DynamoDbConnection(){
    }

    //may only be used for getting a single item. Will want to batch fetch when getting larger list
    String getAvailableBakedGoods(){
        attribute.clear();
        Map<String, AttributeValue> response = new HashMap<>();
        attribute.put(ItemKeys.Component.name(), AttributeValue.builder().s(ItemKeys.AvailableGoods.name()).build());
        List<AttributeValue> attributeList = new LinkedList<>();
        try{
            GetItemRequest request = GetItemRequest.builder()
                    .tableName(Tables.StoreFront.name())
                    .key(attribute)
                    .projectionExpression("Baked")
                    .build();

            response = client.getItem(request).item();

            response.values().forEach(val->{
                for (AttributeValue item: val.l()) {
                    attributeList.add(item);
                }
            });

        }catch (DynamoDbException e){

        }
        Set<String> availableItem = new HashSet<>();
        attributeList.forEach(attr->{
            availableItem.add(attr.s());
        });
        return gson.toJson(availableItem);
    }

    void addAvailableBakedGoods(String item) throws FileNotFoundException {
        attribute.clear();

        //temporary fix until i figure out expressions
        attribute.put(ItemKeys.Component.name(), AttributeValue.builder().s(ItemKeys.AvailableGoods.name()).build());
        List<AttributeValue> values = new LinkedList<>();
        convertAvailableItem().get("Baked").forEach(good->{
            values.add(AttributeValue.builder().s(good).build());
        });
        values.add(AttributeValue.builder().s(item).build());

        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();

        updatedValues.put("Baked", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().l(values).build())
                .action(AttributeAction.PUT)
                .build());


        try{
            UpdateItemRequest request = UpdateItemRequest.builder()
                    .tableName(Tables.StoreFront.name())
                    .key(attribute)
                    .attributeUpdates(updatedValues)
                    .build();
            client.updateItem(request);
        }catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    Map<String,List<String>> convertAvailableItem() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(AVAILABLEITEM));
        Map<String, List<String>> result = gson.fromJson(reader, Map.class);
        return result;
    }
}
