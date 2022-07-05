import com.tecton.client.TectonClient;
import com.tecton.client.model.FeatureValue;
import com.tecton.client.model.ValueType;
import com.tecton.client.request.GetFeaturesRequest;
import com.tecton.client.request.GetFeaturesRequestData;
import com.tecton.client.response.GetFeaturesResponse;

import java.time.Duration;
import java.util.Map;

public class TectonClientSimpleDemo {

  public static void main(String[] args) {

    String url = "https://staging.tecton.ai";
    String apiKey = "da9dda5c176494883455ef579a38bd29";

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

    //Network + Online Store Latency
    Duration latency = getFeaturesResponse.getRequestLatency();

    //Get Feature Vector
    //List<FeatureValue> featureValues = getFeaturesResponse.getFeatureValues();
    Map<String, FeatureValue> featureValues = getFeaturesResponse.getFeatureValuesAsMap();

    //Access Feature Name and value
    FeatureValue sampleFeatureValue = featureValues.get("user_transaction_amount_metrics.amt_mean_1d_10m");
    String featureNamespace = sampleFeatureValue.getFeatureNamespace();
    String featureName = sampleFeatureValue.getFeatureName();
    ValueType valueType = sampleFeatureValue.getValueType();
    Double value = sampleFeatureValue.float64Value();
  }
}
