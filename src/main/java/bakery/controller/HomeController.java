package bakery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private HomeController(){

    }

    @GetMapping("/")
    public String Index(){
        //list of bakers'
        return null;
    }
}
