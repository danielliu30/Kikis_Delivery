package bakery.Services;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.*;

@Service
public class DynamoDbConnection {

    private static final DynamoDbClient client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    private static final DynamoDbEnhancedClient enahancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    private static final Gson gson = new Gson();

    private static HashMap<String, AttributeValue> attribute = new HashMap<>();
    private enum ItemKeys{Component, AvailableGoods, BakedItem, Variation}
    private enum Tables{BakedGoods, StoreFront}

    private DynamoDbConnection(){
    }

    String getAvailableBakedGoods(){
        attribute.clear();
        Map<String, AttributeValue> response = new HashMap<>();
        attribute.put(ItemKeys.Component.name(), AttributeValue.builder().s(ItemKeys.AvailableGoods.name()).build());
        List<AttributeValue> aaa = new LinkedList<>();
        try{
            GetItemRequest request = GetItemRequest.builder()
                    .tableName(Tables.StoreFront.name())
                    .key(attribute)
                    .projectionExpression("Baked")
                    .build();

            response = client.getItem(request).item();

            response.values().forEach(val->{
                for (AttributeValue item: val.l()) {
                    aaa.add(item);
                }
            });

        }catch (DynamoDbException e){

        }
        Set<String> availableItem = new HashSet<>();
        aaa.forEach(attr->{
            availableItem.add(attr.s());
        });
        return gson.toJson(availableItem);
    }
}
