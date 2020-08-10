package bakery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.WebSecurityEnablerConfiguration;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class StartUp /* extends WebSecurityEnablerConfiguration */{
    public static void main(String[] args){
        SpringApplication.run(StartUp.class, args);
    }
    
  
}
 