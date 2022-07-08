import com.tecton.client.TectonClient;
import com.tecton.client.model.FeatureServiceMetadata;
import com.tecton.client.model.NameAndType;
import com.tecton.client.model.ValueType;
import com.tecton.client.request.GetFeatureServiceMetadataRequest;
import com.tecton.client.response.GetFeatureServiceMetadataResponse;

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
        GetFeatureServiceMetadataRequest request = new GetFeatureServiceMetadataRequest("fraud_detection_feature_service", "prod");

        //Send request and receive response
        GetFeatureServiceMetadataResponse response = tectonClient.getFeatureServiceMetadata(request);

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
