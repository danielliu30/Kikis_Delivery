package bakery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
