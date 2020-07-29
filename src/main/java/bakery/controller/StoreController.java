package bakery.controller;

import bakery.Models.BakedItem;
import bakery.Models.SingleCustomer;
import bakery.Services.BakedFormation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("store")
public class StoreController {

    private final BakedFormation bakedFormation;
    private final CacheControl defaultCache =  CacheControl.maxAge(30, TimeUnit.MINUTES);
    @Autowired
    private StoreController(BakedFormation bakedFormation){
        this.bakedFormation = bakedFormation;
    }

    @GetMapping("")
    public ResponseEntity<String> Index() {
    	
    	return ResponseEntity.ok().cacheControl(defaultCache)
    			.body(bakedFormation.getCategories());
    }
    @GetMapping("/{category}")
    public ResponseEntity<String> getCategoryList(@PathVariable String category){
        return ResponseEntity.ok().cacheControl(defaultCache)
        		.body(bakedFormation.getAvailableBakedItems(category));
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
    
  //this http cache only work on msft edge, need to change defualt cahce settings
    @GetMapping("/customerList")
    public ResponseEntity<String> getAllCustomers(){
    	return ResponseEntity.ok().cacheControl(defaultCache)
    			.body(bakedFormation.getAllCustomers());
    }
}
