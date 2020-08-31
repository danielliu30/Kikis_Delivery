package bakery.Services;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Service
class S3Connection {
    private final static S3Client client = S3Client.builder().region(Region.US_EAST_2).build();
    private final String path = "C:\\Users\\barney\\IdeaProjects\\bakery.bakeshop\\src\\main\\resources\\s3CategoryList.json";
    
    private static final Gson gson = new Gson();
    private S3Connection (){

    }

//    void uploadItem(){
//        File f = new File(path);
//        try{
//            PutObjectResponse response  = client.putObject(
//                    PutObjectRequest.builder()
//                    .bucket("ris-tester-123-123-44455")
//                    .key("key")
//                    .build(),
//                    RequestBody.fromFile(f));
//
//        }catch (S3Exception e){
//
//        }
//    }
    //potential update with string instead of local file that doesn't even exist lol
    void uploadItem(){
        File f = new File(path);
        try{
            PutObjectResponse response  = client.putObject(
                    PutObjectRequest.builder()
                    .bucket("ris-tester-123-123-44455")
                    .key("key")
                    .build(),
                    RequestBody.fromFile(f));

        }catch (S3Exception e){

        }
    }
    Map<String, List<String>> retrieveCategoryList(){
             
    	ResponseInputStream<GetObjectResponse> object = null;
		try {
			
 			object = client.getObject(GetObjectRequest.builder().bucket("ris-tester-123-123-44455").key("key").build(),
					ResponseTransformer.toInputStream());
        }catch (S3Exception e){

        }
		Reader reader = new BufferedReader(new InputStreamReader(object,Charset.forName(StandardCharsets.UTF_8.name())));
		Map<String,List<String>> test = gson.fromJson(reader, Map.class);
        return test;
    }
    void createBucket(){
        try{
            CreateBucketRequest createBucketRequest = CreateBucketRequest
                    .builder()
                    //this bucket name has to be uniquely global
                    .bucket("ris-tester-123-123-44455")
                    .createBucketConfiguration(CreateBucketConfiguration.builder()
                            .locationConstraint(Region.US_EAST_2.id())
                            .build())
                    .build();
            client.createBucket(createBucketRequest);

        }catch (S3Exception e){

        }

    }

    public void deleteBucket() {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket("ris-tester").build();
        client.deleteBucket(deleteBucketRequest);
    }

}
