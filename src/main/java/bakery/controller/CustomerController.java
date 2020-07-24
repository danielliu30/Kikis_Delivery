package bakery.controller;

import java.util.Set;

import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bakery.Services.BakedFormation;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	private static BakedFormation bakedFormation;
	
	private CustomerController(BakedFormation bakedFormation) {
		CustomerController.bakedFormation = bakedFormation;
	}
}
	
	
