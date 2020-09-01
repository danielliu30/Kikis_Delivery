package bakery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bakery.Models.JwtRequest;
import bakery.Models.SingleCustomer;
import bakery.Services.BakedFormation;
import bakery.Services.Security.TokenUtil;


/**
 * End points for customers to access
 * @author barney
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private static BakedFormation bakedFormation;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	
	private CustomerController(BakedFormation bakedFormation) {
		CustomerController.bakedFormation = bakedFormation;
	}

	//generates JWT when sign in
	@CrossOrigin(origins = "http://localhost:3000")
	@RequestMapping(method = RequestMethod.POST, path = "/signIn")
	public String LoginAccount(@RequestBody JwtRequest customer) {
		return tokenUtil.generateToken(customer.getUserName());
	}

	//generates account confirmation via email
	@RequestMapping(method = RequestMethod.POST, path = "/signUp")
	public void CreateAccount(@RequestBody SingleCustomer customer){
		if(!bakedFormation.checkExisitingUser(customer)){
			bakedFormation.sendVerificationToken(bakedFormation.genreateValidationToken(customer), customer.getEmail());
			//send email
		}
	}

	//end point for users to hit when they are validatin via email.
	//made get for testing purposes
	@RequestMapping(method = RequestMethod.GET, path="/verifiedToken-{token}")
	public ResponseEntity<Boolean> VerifyAccount(@PathVariable String token){
		return ResponseEntity.ok().body(bakedFormation.checkValidationToken(token)); 
	}
	// validiation is taken care of in the RequestFilter
	// NEed to add more validation for the sign in portion

}
