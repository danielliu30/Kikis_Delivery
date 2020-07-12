package bakery.controller;

import bakery.Services.BakedFormation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
public class HomeController {

    private final BakedFormation bakedFormation;
    @Autowired
    private HomeController(BakedFormation bakedFormation){
        this.bakedFormation = bakedFormation;
    }

    @GetMapping("/")
    public String Index(){
        return bakedFormation.getAvailableBakedItems();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/addItem", params = "type")
    public String AddStoreItem(@RequestParam("type") String type) {
        try{
            bakedFormation.addAvailableBakedItems(type);
        }catch (DynamoDbException e1){
            return e1.getMessage();
        }catch (FileNotFoundException e2){
            return e2.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Successfully Added";
    }
}
