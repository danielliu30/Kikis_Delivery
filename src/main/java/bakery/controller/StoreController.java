package bakery.controller;

import bakery.Models.BakedItem;
import bakery.Models.SingleCustomer;
import bakery.Services.BakedFormation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("store")
public class StoreController {

    private final BakedFormation bakedFormation;
    @Autowired
    private StoreController(BakedFormation bakedFormation){
        this.bakedFormation = bakedFormation;
    }

    @GetMapping("/")
    public String Index(){
        return bakedFormation.getAvailableBakedItems();
    }

    //probably want to add bake item models for json mapping
    @RequestMapping(method = RequestMethod.POST, path = "/addItem")
    public String AddStoreItem(@RequestBody BakedItem bakedItem) throws IOException {
        bakedFormation.addAvailableBakedItems(bakedItem);
        return "Successfully Added";
    }

    //should be post but was testing S3 operations
	@GetMapping(value = "/upload"/* , headers = "application/json" */)
    public void uploadItem(){
        bakedFormation.uploadFile();
    }

    @GetMapping("/menu")
    public Map<String, Object> getMenu(){
    	Map<String,Object> item = new HashMap<>();
    	item.put("Name", "Stacey");
    	item.put("Link", new Link("home","http://localhost:8080/"));
        return item;
    }
    
    @RequestMapping(method = RequestMethod.POST, path = "/addCustomer")
    public String addCustomer(@RequestBody SingleCustomer customer) {
    	try {
    		bakedFormation.addCustomer(customer);	
    	}catch(Exception e) {
    		//want to do the chained exception thingy
    		return e.getMessage();
    	}
    	
    	return "Added Customer";
    }
}
