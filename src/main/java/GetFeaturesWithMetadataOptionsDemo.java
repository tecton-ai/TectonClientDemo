import ai.tecton.client.TectonClient;
import ai.tecton.client.model.FeatureValue;
import ai.tecton.client.model.SloInformation;
import ai.tecton.client.request.GetFeaturesRequest;
import ai.tecton.client.request.GetFeaturesRequestData;
import ai.tecton.client.request.RequestConstants;
import ai.tecton.client.response.GetFeaturesResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class GetFeaturesWithMetadataOptionsDemo {

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("tecton.properties"));
        String url = properties.getProperty("url");
        String apiKey = properties.getProperty("apiKey");

        // Create Tecton Client
        TectonClient tectonClient = new TectonClient(url, apiKey);

        // Create Request Data
        GetFeaturesRequestData getFeaturesRequestData =
                new GetFeaturesRequestData()
                        .addJoinKey("user_id", "user_205125746682")
                        .addJoinKey("merchant", "entertainment")
                        .addRequestContext("amt", 500.00);

        // Create Request
        GetFeaturesRequest getFeaturesRequest =
                new GetFeaturesRequest("prod", "fraud_detection_feature_service", getFeaturesRequestData, RequestConstants.ALL_METADATA_OPTIONS);


        // Send request and receive response
        GetFeaturesResponse getFeaturesResponse = tectonClient.getFeatures(getFeaturesRequest);

        // Get Feature Vector as Map
        Map<String, FeatureValue> featureValues = getFeaturesResponse.getFeatureValuesAsMap();

        //Print Feature Values
        for (FeatureValue featureValue : featureValues.values()) {
            System.out.println("\nFeature Namespace: " + featureValue.getFeatureNamespace());
            System.out.println("Feature Name: " + featureValue.getFeatureName());
            System.out.println("Data Type: " + featureValue.getValueType().name());
            System.out.println("Status: "+ featureValue.getFeatureStatus().get());
            if (featureValue.getEffectiveTime().isPresent()) {
                System.out.println("Effective Time: " + featureValue.getEffectiveTime().get());
            }
            switch (featureValue.getValueType()) {
                case STRING:
                    System.out.println("Feature Value: " + featureValue.stringValue());
                    break;
                case FLOAT64:
                    System.out.println("Feature Value: " + featureValue.float64Value());
                    break;
                case INT64:
                    System.out.println("Feature Value: " + featureValue.int64value());
                    break;
                case BOOLEAN:
                    System.out.println("Feature Value: " + featureValue.booleanValue());
            }

        }

        // Print Slo Information
        if (getFeaturesResponse.getSloInformation().isPresent()) {
            SloInformation sloInfo = getFeaturesResponse.getSloInformation().get();
            if (sloInfo.isSloEligible().isPresent()) {
                System.out.println("\nisSloEligible: " + sloInfo.isSloEligible().get());
            }
            if(sloInfo.getServerTimeSeconds().isPresent()) {
                System.out.println("serverTimeSeconds: "+sloInfo.getServerTimeSeconds().get());
            }
            if(sloInfo.getSloServerTimeSeconds().isPresent()) {
                System.out.println("sloServerTimeSeconds: "+sloInfo.getSloServerTimeSeconds().get());
            }
        }
    }
}
