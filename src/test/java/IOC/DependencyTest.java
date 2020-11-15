package IOC;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import bakery.StartUp;
import bakery.Services.BakedFormation;
import bakery.controller.CustomerController;
import bakery.controller.StoreController;

@RunWith(SpringRunner.class)
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
