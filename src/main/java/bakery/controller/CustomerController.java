package bakery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import bakery.Models.JwtRequest;

import bakery.Services.BakedFormation;
import bakery.Services.Security.TokenUtil;

@RestController
@RequestMapping("/customer")
public class CustomerController {

	private static BakedFormation bakedFormation;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	
	private CustomerController(BakedFormation bakedFormation) {
		CustomerController.bakedFormation = bakedFormation;
	}

	@RequestMapping(method = RequestMethod.POST, path = "/createAccount")
	public String CreateAccount(@RequestBody JwtRequest customer) {
		try {
			authenticate(customer.getUserName(), customer.getPassword());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String token = tokenUtil.generateToken(customer.getUserName());
		return token;
	}

	@PostMapping("/signIn")
	public String SignIn(@RequestBody String token) {
		return "You are through";
	}

	private void authenticate(String username, String password) throws Exception {
		//authenticated
	}
}
