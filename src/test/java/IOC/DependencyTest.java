package IOC;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import bakery.StartUp;
import bakery.Services.BakedFormation;
import bakery.controller.CustomerController;
import bakery.controller.StoreController;

@SpringBootTest(classes = StartUp.class)
public class DependencyTest {


    @Autowired
    private CustomerController customerController;
    @Autowired
    private StoreController storeController;
    @Autowired
    private BakedFormation bakedFormation;
    
    @Test
    public void customerControllerDependencyTest(){
        assertTrue(customerController != null);
    }
    @Test
    public void storeControllerDependencyTest(){
        assertTrue(storeController != null);
    }
    @Test
    public void bakedFormationDependencyTest(){
        assertTrue(bakedFormation != null);
    }
    @Test
    public void dynamoDependencyTest(){
        assertTrue(bakedFormation.getDynamoDbConnection() != null);
    }
    @Test
    public void emailConnectionDependencyTest(){
        assertTrue(bakedFormation.getEmailConnection() != null);
    }
}
