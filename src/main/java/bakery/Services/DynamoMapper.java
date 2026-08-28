package bakery.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import bakery.Models.BakedGoods;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Component
class DynamoMapper {

    private static final String TABLE_NAME = "BakedGoods";

    private static final TableSchema<BakedGoods> SCHEMA = StaticTableSchema.builder(BakedGoods.class)
            .newItemSupplier(BakedGoods::new)
            .addAttribute(String.class, a -> a.name("BakedItem")
                    .getter(item -> item.BakedItem)
                    .setter((item, value) -> item.BakedItem = value)
                    .tags(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("ItemVariation")
                    .getter(item -> item.ItemVariation)
                    .setter((item, value) -> item.ItemVariation = value)
                    .tags(StaticAttributeTags.primarySortKey()))
            .addAttribute(String.class, a -> a.name("size")
                    .getter(item -> item.size)
                    .setter((item, value) -> item.size = value))
            .addAttribute(String.class, a -> a.name("shape")
                    .getter(item -> item.shape)
                    .setter((item, value) -> item.shape = value))
            .addAttribute(String.class, a -> a.name("count")
                    .getter(item -> item.count)
                    .setter((item, value) -> item.count = value))
            .addAttribute(String.class, a -> a.name("flavor")
                    .getter(item -> item.flavor)
                    .setter((item, value) -> item.flavor = value))
            .addAttribute(String.class, a -> a.name("layers")
                    .getter(item -> item.layers)
                    .setter((item, value) -> item.layers = value))
            .addAttribute(String.class, a -> a.name("calories")
                    .getter(item -> item.calories)
                    .setter((item, value) -> item.calories = value))
            .addAttribute(String.class, a -> a.name("toppings")
                    .getter(item -> item.toppings)
                    .setter((item, value) -> item.toppings = value))
            .addAttribute(String.class, a -> a.name("fillings")
                    .getter(item -> item.fillings)
                    .setter((item, value) -> item.fillings = value))
            .addAttribute(String.class, a -> a.name("vegan")
                    .getter(item -> item.vegan)
                    .setter((item, value) -> item.vegan = value))
            .addAttribute(String.class, a -> a.name("glutenFree")
                    .getter(item -> item.glutenFree)
                    .setter((item, value) -> item.glutenFree = value))
            .addAttribute(String.class, a -> a.name("expirationTime")
                    .getter(item -> item.expirationTime)
                    .setter((item, value) -> item.expirationTime = value))
            .addAttribute(String.class, a -> a.name("cost")
                    .getter(item -> item.cost)
                    .setter((item, value) -> item.cost = value))
            .build();

    private static final DynamoDbClient dbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    private static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dbClient)
            .build();
    private static final DynamoDbTable<BakedGoods> table = enhancedClient.table(TABLE_NAME, SCHEMA);

    /**
     * @throws IllegalArgumentException if a key attribute of the BakedGoods table is
     *                                  missing, since DynamoDB would reject the write
     */
    void addBakedItem(BakedGoods item) {
        List<String> missing = new ArrayList<>();
        if (isBlank(item.BakedItem)) {
            missing.add("BakedItem");
        }
        if (isBlank(item.ItemVariation)) {
            missing.add("ItemVariation");
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing required field(s): " + String.join(", ", missing));
        }
        table.putItem(item);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
