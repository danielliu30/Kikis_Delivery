package bakery.controller;

import org.jboss.logging.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@RequestMapping(method = RequestMethod.POST, path = "/signIn")
	public String LoginAccount(@RequestBody JwtRequest customer) {
		//validate password length and email length on react side
		//generate random token, save the token for 24 hours

		//add endpoint to validate with the token as a param
		//send email with token link to verify and store new user
		//use lambda func to destroy token
		//add some sort of error handling for this
		return tokenUtil.generateToken(customer.getUserName());
	}

	@RequestMapping(method = RequestMethod.POST, path = "/signUp")
	public void CreateAccount(@RequestBody SingleCustomer customer){
		if(!bakedFormation.checkExisitingUser(customer)){
			bakedFormation.genreateValidationToken();
			//send email
		}
	}

	@RequestMapping(method = RequestMethod.POST, path="/verifiedToken-{token}")
	public void VerifyAccount(@PathVariable String token){
		//deleteToken and add user 
	}
	//validiation is taken care of in the RequestFilter
	//NEed to add more validation for the sign in portion

}
