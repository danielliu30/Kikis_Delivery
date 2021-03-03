package bakery.Services;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import org.springframework.stereotype.Component;

import bakery.Models.BakedGoods;


@Component
class DynamoMapper {
    private static AmazonDynamoDB dbClient = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    private static DynamoDBMapper mapper = new DynamoDBMapper(dbClient);
    
    void addBakedItem(BakedGoods item){
        mapper.save(item);
    }
}
