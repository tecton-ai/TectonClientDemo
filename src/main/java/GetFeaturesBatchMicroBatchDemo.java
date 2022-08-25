import ai.tecton.client.TectonClient;
import ai.tecton.client.TectonClientOptions;
import ai.tecton.client.exceptions.TectonClientException;
import ai.tecton.client.exceptions.TectonServiceException;
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
		//Note: It is recommended to keep the maxParallelRequests configuration at 5 for microBatchSize > 1 to avoid 429 error responses
		TectonClientOptions clientOptions = new TectonClientOptions.Builder().maxParallelRequests(5).build();
		TectonClient tectonClient = new TectonClient(url, apiKey, clientOptions);


		//Create a GetFeaturesBatchRequest with 200 requestData and microBatchSize of 5
		List<GetFeaturesRequestData> getFeaturesRequestDataList = GetFeaturesBatchDemo.generateFraudRequestDataFromFile("input.csv");
		GetFeaturesBatchRequest batchRequest = new GetFeaturesBatchRequest(WORKSPACE_NAME, FEATURE_SERVICE_NAME, getFeaturesRequestDataList, RequestConstants.DEFAULT_METADATA_OPTIONS, 5);

		try {
			GetFeaturesBatchResponse batchResponse = tectonClient.getFeaturesBatch(batchRequest);
		} catch (TectonClientException | TectonServiceException e) {
			e.printStackTrace();
		}

		tectonClient.close();
	}
}
