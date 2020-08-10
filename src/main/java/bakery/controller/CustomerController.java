package bakery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bakery.Models.JwtRequest;

import bakery.Services.BakedFormation;

/**
 * End points for customers to access
 * @author barney
 *
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

	private static BakedFormation bakedFormation;
	
//	@Autowired
//	private TokenUtil tokenUtil;
//	
	
	private CustomerController(BakedFormation bakedFormation) {
		CustomerController.bakedFormation = bakedFormation;
	}

//	@RequestMapping(method = RequestMethod.POST, path = "/createAccount")
//	public String CreateAccount(@RequestBody JwtRequest customer) {
//		
//		//add some sort of error handling for this
//		return tokenUtil.generateToken(customer.getUserName());
//	}

	//validiation is taken care of in the RequestFilter
	//NEed to add more validation for the sign in portion
	@PostMapping("/signIn")
	public String SignIn() {
		return "You are through";
	}

}
