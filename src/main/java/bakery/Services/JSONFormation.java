package bakery.Services;

import org.springframework.stereotype.Service;

@Service
public class JSONFormation {

    private final DynamoDbConnection dbConnection;
    private JSONFormation(DynamoDbConnection dbConnection){
        this.dbConnection = dbConnection;
    }

    String configureAvailableGoods(){
        return dbConnection.getAvailableBakedGoods();
    }
}
