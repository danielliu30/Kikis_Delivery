package bakery.Services;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

@Service
public class JSONFormation {

    private final DynamoDbConnection dbConnection;
    private JSONFormation(DynamoDbConnection dbConnection){
        this.dbConnection = dbConnection;
    }


    private static final Gson gson = new Gson();
    String configureAvailableGoods(){
        //may want to modify json output format
        return dbConnection.getAvailableBakedGoods();
    }

    void validateAddItem(String request) throws FileNotFoundException {
        //may need to modify incoming string
        dbConnection.addAvailableBakedGoods(request);
    }


}
