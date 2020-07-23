package bakery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import bakery.Models.BakedItem;
import bakery.Models.SingleCustomer;

@Service
public class BakedFormation {


	private final DynamoDbConnection dbConnection;
	private final S3Connection s3Connection;
    @Autowired
    private BakedFormation(DynamoDbConnection dbConnection, S3Connection s3Connection){
        this.dbConnection = dbConnection;
        this.s3Connection = s3Connection;
    }

    
    public String getAvailableBakedItems(){
        return dbConnection.getAvailableBakedGoods();
    }

    public void addAvailableBakedItems(BakedItem item) throws JsonProcessingException{
        dbConnection.addAvailableBakedGoods(item);;
    }

    public void uploadFile(){
        s3Connection.uploadItem();
    }

    public String getMenu(){
        return s3Connection.retrieveBucketItem();
    }
    
    public void addCustomer(SingleCustomer customer){
    	//validate email
    	dbConnection.addCustomerMember(customer);
    }
}
