import ai.tecton.client.TectonClient;
import ai.tecton.client.TectonClientOptions;
import ai.tecton.client.request.GetFeaturesBatchRequest;
import ai.tecton.client.request.GetFeaturesRequestData;
import ai.tecton.client.request.RequestConstants;
import ai.tecton.client.response.GetFeaturesBatchResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class GetFeaturesBatchMicroBatchDemo {

	private static final String WORKSPACE_NAME = "prod";
	private static final String FEATURE_SERVICE_NAME = "fraud_detection_feature_service";

	public static void main(String[] args) throws IOException, InterruptedException {

		//Read URL and api key from tecton.properties file
		Properties properties = new Properties();
		properties.load(new FileInputStream("tecton.properties"));
		String url = properties.getProperty("url");
		String apiKey = properties.getProperty("apiKey");


		// Create Tecton Client
		//Note: Keep the maxParallelRequests configuration at 5 for microBatchSize>1 to avoid 429 error responses
		TectonClientOptions clientOptions = new TectonClientOptions.Builder().maxParallelRequests(5).build();
		TectonClient tectonClient = new TectonClient(url, apiKey, clientOptions);

		List<GetFeaturesRequestData> getFeaturesRequestDataList = GetFeaturesBatchDemo.generateFraudRequestDataFromFile("input.csv");

		//Create a GetFeaturesBatchRequest with 100 requestData and microBatchSize of 5
		GetFeaturesBatchRequest batchRequest = new GetFeaturesBatchRequest(WORKSPACE_NAME, FEATURE_SERVICE_NAME, getFeaturesRequestDataList, RequestConstants.DEFAULT_METADATA_OPTIONS, 5);

		GetFeaturesBatchResponse batchResponse = tectonClient.getFeaturesBatch(batchRequest);

		//REFER TO GetFeaturesDemo on how to access each GetFeaturesResponse in the responseList
		/*
		responseList.forEach(getFeaturesResponse -> {

			//Get each Feature Vector as List
			List<FeatureValue> featureValueList = getFeaturesResponse.getFeatureValues();

			//Get Feature Vector as Map
			Map<String, FeatureValue> featureValues = getFeaturesResponse.getFeatureValuesAsMap();

			//Access Individual Feature Names and values
			FeatureValue sampleFeatureValue = featureValues.get("user_transaction_amount_metrics.amt_mean_1d_10m");
			String featureNamespace = sampleFeatureValue.getFeatureNamespace();
			String featureName = sampleFeatureValue.getFeatureName();
			ValueType valueType = sampleFeatureValue.getValueType();
			Double value = sampleFeatureValue.float64Value();
		});
		*/

		tectonClient.close();
	}
}
