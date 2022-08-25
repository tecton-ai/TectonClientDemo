
import ai.tecton.client.TectonClient;
import ai.tecton.client.exceptions.TectonClientException;
import ai.tecton.client.exceptions.TectonServiceException;
import ai.tecton.client.model.FeatureValue;
import ai.tecton.client.model.ValueType;
import ai.tecton.client.request.GetFeaturesRequest;
import ai.tecton.client.request.GetFeaturesRequestData;
import ai.tecton.client.response.GetFeaturesResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetFeaturesDemo {


	private static final String WORKSPACE_NAME = "prod";
	private static final String FEATURE_SERVICE_NAME = "fraud_detection_feature_service";

	public static void main(String[] args) throws IOException {


		Properties properties = new Properties();
		properties.load(new FileInputStream("tecton.properties"));
		String url = properties.getProperty("url");
		String apiKey = properties.getProperty("apiKey");
		//String apiKey = "1234";

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
				new GetFeaturesRequest(WORKSPACE_NAME, FEATURE_SERVICE_NAME, getFeaturesRequestData);


		// Send request and receive response
		//We recommend surrounding it in a try-catch block as the client is expected to throw TectonClientException / TectonServiceException on errors
		try {
			GetFeaturesResponse getFeaturesResponse = tectonClient.getFeatures(getFeaturesRequest);

			//Get Feature Vector as List
			List<FeatureValue> featureValueList = getFeaturesResponse.getFeatureValues();

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
				System.out.println("Value Type: " + featureValue.getValueType());
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
		} catch (TectonClientException | TectonServiceException e) {
			e.printStackTrace();
		}

		//Close the client to release resources such as ConnectionPool, threadpool etc. A client cannot be reopened/reused once its closed
		tectonClient.close();
	}
}
