package bakery.controller;

import bakery.Services.BakedFormation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

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
    public void AddStoreItem(@RequestParam("type") String type) throws FileNotFoundException {
        bakedFormation.addAvailableBakedItems(type);
    }
}
