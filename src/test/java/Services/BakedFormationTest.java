package Services;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bakery.StartUp;
import bakery.Services.BakedFormation;

@SpringBootTest(classes = StartUp.class)
public class BakedFormationTest {
    
    @Autowired
    private BakedFormation bakedFormation;
    
    @Test
    public void existing_IndividualCategoryTest(){
        assertNotNull(bakedFormation.getAvailableBakedItems("Cake"));
    }

    @Test
    public void nonExisting_IndividualCategoryTest(){
        assertNotNull(bakedFormation.getAvailableBakedItems("Carrot"));
    }
    @Test
    public void malformedInput1_IndividualCategoryTest(){
        assertNotNull(bakedFormation.getAvailableBakedItems("cake"));
    }

    @Test
    public void malformedInput2_IndividualCategoryTest(){
        assertNotNull(bakedFormation.getAvailableBakedItems("cAKE"));
    }

    @Test
    public void malformedInput3_IndividualCategoryTest(){
        assertNotNull(bakedFormation.getAvailableBakedItems(""));
    }
    
    @Test
    public void malformedInput4_IndividualCategoryTest(){
        assertNotNull(bakedFormation.getAvailableBakedItems(null));
    }
}
