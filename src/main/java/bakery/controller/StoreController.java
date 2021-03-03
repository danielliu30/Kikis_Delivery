package bakery.controller;

import bakery.Models.BakedGoods;
import bakery.Models.SingleCustomer;
import bakery.Services.BakedFormation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

/**
 * End points for store owners/employees to access
 * 
 * @author barney
 *
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("store")
public class StoreController {

    private final BakedFormation bakedFormation;

    /**
     * default cache time set for the client
     */
    private final CacheControl defaultCache = CacheControl.maxAge(30, TimeUnit.MINUTES);

    @Autowired
    private StoreController(BakedFormation bakedFormation) {
        this.bakedFormation = bakedFormation;
    }

    /**
     * Displays a JSON array of high level categories for baked goods and
     * customers,each with links to more detailed end points
     * 
     * @return a string in JSON format
     */
    @GetMapping("")
    public ResponseEntity<String> Index() {

        return ResponseEntity.ok().cacheControl(defaultCache).body(bakedFormation.getCategories());
    }

    /**
     * Displays a JSON array of all baked items in a specific category
     * 
     * @param category specific group to be selected
     * @return a String in JSON format
     */
    @GetMapping(path = "/{category}")
    public ResponseEntity<String> getCategoryList(@PathVariable String category) {
        return ResponseEntity.ok().cacheControl(defaultCache).body(bakedFormation.getAvailableBakedItems(category));
    }

    /**
     * Reads a JSON object and deserializes into a BakedItem POJO for storage
     * purposes
     * 
     * @param bakedItem baked good that is wanting to be added into the system
     * @return a string that notifies success/failure of the addition
     * @throws IOException if JSON serialization fails
     */
    // probably want to add bake item models for json mapping
    @PostMapping(path = "/addItem")
    public ResponseEntity<String> AddStoreItem(@RequestBody BakedGoods bakedItem) throws IOException {
        bakedFormation.addAvailableBakedItems(bakedItem);
        return ResponseEntity.ok().body("item successfully added");
    }

    // should be post but was testing S3 operations
    @GetMapping(path = "/upload"/* , headers = "application/json" */)
    public void uploadItem() {
        bakedFormation.uploadFile();
    }

    /**
     * Reads a JSON object and deserializes into a customer for storage purposes
     * 
     * @param customer new customer that is enrolling into system
     * @return a String to signal for the success/failure of adding a customer
     */
    @PostMapping(path = "/addCustomer")
    public ResponseEntity<String> addCustomer(@RequestBody SingleCustomer customer) {
        try {
            bakedFormation.addCustomer(customer);
        } catch (Exception e) {
            // want to do the chained exception thingy
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("customer successfully added");
    }

    /**
     * Display a list of all existing customers in a JSON formatted String
     * 
     * @return a JSON String of all customers
     */
    //limit nbumber of customers returned to prevent ddos
    @GetMapping(path = "/customerList")
    public ResponseEntity<String> getAllCustomers() {
        return ResponseEntity.ok().cacheControl(defaultCache).body(bakedFormation.getAllCustomers());
    }

    //delete customer. May need to change.
    @PostMapping(value = "/deleteCustomer/{key}/{value}")
    private ResponseEntity<String> deleteCustomer(@PathVariable String key, @PathVariable String value) {
        try {
            bakedFormation.deleteCustomer(key, value);
        } catch (Exception e) {
            //log request
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body("customer successfully deleted");
    }
}
