import ai.tecton.client.TectonClient;
import ai.tecton.client.TectonClientOptions;
import ai.tecton.client.exceptions.TectonClientException;
import ai.tecton.client.exceptions.TectonServiceException;
import ai.tecton.client.request.GetFeaturesBatchRequest;
import ai.tecton.client.request.GetFeaturesRequestData;
import ai.tecton.client.request.RequestConstants;
import ai.tecton.client.response.GetFeaturesBatchResponse;
import ai.tecton.client.response.GetFeaturesResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class GetFeaturesBatchTimeoutDemo {

	private static final String WORKSPACE_NAME = "prod";
	private static final String FEATURE_SERVICE_NAME = "fraud_detection_feature_service";

	public static void main(String[] args) throws IOException {

		//Read URL and api key from tecton.properties file
		Properties properties = new Properties();
		properties.load(new FileInputStream("tecton.properties"));
		String url = properties.getProperty("url");
		String apiKey = properties.getProperty("apiKey");


		// Create Default Tecton Client
		TectonClientOptions clientOptions = new TectonClientOptions.Builder().maxParallelRequests(5).build();
		TectonClient tectonClient = new TectonClient(url, apiKey, clientOptions);

		List<GetFeaturesRequestData> getFeaturesRequestDataList = GetFeaturesBatchDemo.generateFraudRequestDataFromFile("input.csv");

		//Create GetFeaturesBatchRequest with 100 request data,  microBatchSize = 1 and a timeout of 1s
		GetFeaturesBatchRequest batchRequest = new GetFeaturesBatchRequest(WORKSPACE_NAME, FEATURE_SERVICE_NAME, getFeaturesRequestDataList, RequestConstants.DEFAULT_METADATA_OPTIONS, 1, Duration.ofSeconds(1));

		//Call getFeaturesBatch using the tectonClient
		try {
			GetFeaturesBatchResponse batchResponse = tectonClient.getFeaturesBatch(batchRequest);
			List<GetFeaturesResponse> responseList = batchResponse.getBatchResponseList();
			System.out.println("\nTotal Successful Responses: " + responseList.stream().filter(Objects::nonNull).count());
		} catch (TectonClientException | TectonServiceException e) {
			e.printStackTrace();
		}

		tectonClient.close();
	}
}
