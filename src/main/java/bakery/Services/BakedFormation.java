package bakery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class BakedFormation {

    private final JSONFormation jsonFormation;


    @Autowired
    private BakedFormation(JSONFormation jsonFormation){
        this.jsonFormation = jsonFormation;
    }

    public String getAvailableBakedItems(){
        return jsonFormation.configureAvailableGoods();
    }

    public void addAvailableBakedItems(String request) throws IOException {
        jsonFormation.validateAddItem(request);
    }
}
