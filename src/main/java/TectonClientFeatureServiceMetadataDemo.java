import ai.tecton.client.TectonClient;
import ai.tecton.client.model.FeatureServiceMetadata;
import ai.tecton.client.model.NameAndType;
import ai.tecton.client.model.ValueType;
import ai.tecton.client.request.GetFeatureServiceMetadataRequest;
import ai.tecton.client.response.GetFeatureServiceMetadataResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TectonClientFeatureServiceMetadataDemo {
    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream("tecton.properties"));
        String url = properties.getProperty("url");
        String apiKey = properties.getProperty("apiKey");

        // Create Tecton Client
        TectonClient tectonClient = new TectonClient(url, apiKey);


        //Create Request
        GetFeatureServiceMetadataRequest request = new GetFeatureServiceMetadataRequest("fraud_detection_feature_service", "pooja-live");


        //Send request and receive response
        long start1 = System.currentTimeMillis();
        GetFeatureServiceMetadataResponse response = tectonClient.getFeatureServiceMetadata(request);
        long stop1 = System.currentTimeMillis();

        long start2 = System.currentTimeMillis();
        GetFeatureServiceMetadataResponse response2 = tectonClient.getFeatureServiceMetadata(request);
        long stop2 = System.currentTimeMillis();

        long start3 = System.currentTimeMillis();
        GetFeatureServiceMetadataResponse response3 = tectonClient.getFeatureServiceMetadata(request);
        long stop3 = System.currentTimeMillis();

        System.out.println("\nRequest 1");
        System.out.println("Total Time: "+(stop1-start1)+"ms");
        System.out.println("Request Latency: "+response.getRequestLatency().toMillis()+"ms");

        System.out.println("\nRequest 2");
        System.out.println("Total Time: "+(stop2-start2)+"ms");
        System.out.println("Request Latency: "+response2.getRequestLatency().toMillis()+"ms");

        System.out.println("\nRequest 3");
        System.out.println("Total Time: "+(stop3-start3)+"ms");
        System.out.println("Request Latency: "+response3.getRequestLatency().toMillis()+"ms");


        //Access metadata
        FeatureServiceMetadata featureServiceMetadata = response.getFeatureServiceMetadata();

        //Metadata as a List
        List<NameAndType> inputJoinKeys = featureServiceMetadata.getInputJoinKeys();
        List<NameAndType> requestContextKeys = featureServiceMetadata.getInputRequestContextKeys();
        List<NameAndType> featureMetadata = featureServiceMetadata.getFeatureValues();

        inputJoinKeys.forEach(joinKey-> {
            String name = joinKey.getName();
            ValueType dataType = joinKey.getDataType();
            //do something
        });

        //Metadata as a map
        Map<String, NameAndType> inputJoinKeyMap = featureServiceMetadata.getInputJoinKeysAsMap();
        Map<String, NameAndType> requestContextKeyMap = featureServiceMetadata.getInputRequestContextKeysAsMap();
        Map<String, NameAndType> featureMetadataMap = featureServiceMetadata.getFeatureValuesAsMap();

        //Print
        System.out.println("\nInput Join Keys");
        printHelper(inputJoinKeys);
        System.out.println("\nInput Request Context Keys");
        printHelper(requestContextKeys);
        System.out.println("\nFeature Value Metadata");
        printHelper(featureMetadata);
    }

    static void printHelper(List<NameAndType> nameAndTypeList) {
        nameAndTypeList.forEach(joinKey -> {
            System.out.println("Name: "+joinKey.getName());
            System.out.println("Value Type: "+joinKey.getDataType());
            joinKey.getListElementType().ifPresent(elementType -> System.out.println("List Element Type: "+elementType.name()));
        });
    }

}
