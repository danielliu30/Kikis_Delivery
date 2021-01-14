package bakery.controller;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bakery.Models.PurchasedItem;
import bakery.Models.SingleCustomer;
import bakery.Services.BakedFormation;
import bakery.Services.Security.TokenUtil;

/**
 * End points for customers to access
 * 
 * @author barney
 *
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/customer")
public class CustomerController {

	// why dont I inject via constructor???????
	@Autowired
	private static BakedFormation bakedFormation;

	@Autowired
	private TokenUtil tokenUtil;

	private static Gson gson = new Gson();

	private CustomerController(BakedFormation bakedFormation) {
		CustomerController.bakedFormation = bakedFormation;
	}

	// generates JWT when sign in
	@PostMapping(path = "/signIn")
	public ResponseEntity<String> LoginAccount(@RequestBody SingleCustomer customer,
			@RequestParam(required = false) String user) throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (bakedFormation.validateLogIn(customer))
			return ResponseEntity.ok().body(gson.toJson(tokenUtil.generateToken(customer.getEmail())));
		return ResponseEntity.badRequest().body(gson.toJson(false));
	}

	// generates account confirmation via email
	@PostMapping(path = "/signUp")
	public ResponseEntity<Boolean> CreateAccount(@RequestBody SingleCustomer customer) {
		if (!bakedFormation.checkExisitingUser(customer)) {
			bakedFormation.sendVerificationToken(bakedFormation.genreateValidationToken(customer), customer.getEmail());
			// send email
			return ResponseEntity.ok().body(true);
		}
		return ResponseEntity.badRequest().body(false);
	}

	// end point for users to hit when they are validatin via email.
	// made get for testing purposes
	@GetMapping(path = "/verifiedToken-{token}")
	public ResponseEntity<Boolean> VerifyAccount(@PathVariable String token) throws JsonProcessingException {
		return ResponseEntity.ok().body(bakedFormation.checkValidationToken(token)); 
	}
	// validiation is taken care of in the RequestFilter
	// NEed to add more validation for the sign in portion
	
	//will get rid of item bought based on its creation time
	@PostMapping(path= "/purchaseItem")
    private void purchasedItem(@RequestBody PurchasedItem item) {
        bakedFormation.deleteStoreItem(item);
	}

}
 