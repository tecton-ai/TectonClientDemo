import com.tecton.client.TectonClient;
import com.tecton.client.model.FeatureValue;
import com.tecton.client.model.ValueType;
import com.tecton.client.request.GetFeaturesRequest;
import com.tecton.client.request.GetFeaturesRequestData;
import com.tecton.client.response.GetFeaturesResponse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class TectonClientSimpleDemo {

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream("tecton.properties"));
        String url = properties.getProperty("url");
        String apiKey = properties.getProperty("apiKey");
        //String apiKey = "12345";

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
                new GetFeaturesRequest("prod", "fraud_detection_feature_service", getFeaturesRequestData);


        // Send request and receive response
        GetFeaturesResponse getFeaturesResponse = tectonClient.getFeatures(getFeaturesRequest);

        //Get Feature Vector as Map
        Map<String, FeatureValue> featureValues = getFeaturesResponse.getFeatureValuesAsMap();

        //Access Individual Feature Names and values
        FeatureValue sampleFeatureValue = featureValues.get("user_transaction_amount_metrics.amt_mean_1d_10m");
        String featureNamespace = sampleFeatureValue.getFeatureNamespace();
        String featureName = sampleFeatureValue.getFeatureName();
        ValueType valueType = sampleFeatureValue.getValueType();
        Double value = sampleFeatureValue.float64Value();

        //Print
        for (FeatureValue featureValue : featureValues.values()) {
            System.out.println("\nFeature Namespace: " + featureValue.getFeatureNamespace());
            System.out.println("Feature Name: " + featureValue.getFeatureName());
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
    }
}
