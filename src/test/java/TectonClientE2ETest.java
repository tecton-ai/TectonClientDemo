import ai.tecton.client.TectonClient;
import ai.tecton.client.TectonClientOptions;
import ai.tecton.client.model.FeatureServiceMetadata;
import ai.tecton.client.model.FeatureValue;
import ai.tecton.client.model.NameAndType;
import ai.tecton.client.model.ValueType;
import ai.tecton.client.request.*;
import ai.tecton.client.response.GetFeatureServiceMetadataResponse;
import ai.tecton.client.response.GetFeaturesBatchResponse;
import ai.tecton.client.response.GetFeaturesResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TectonClientE2ETest {

	private String TECTON_URL;
	private String TECTON_API_KEY;
	private static String WORKSPACE_NAME;
	private static String FEATURE_SERVICE_NAME;
	private TectonClient tectonClient;
	private static final String FEATURE_NAMESPACE = "user_transaction_counts";
	private static final String FEATURE_NAME = "transaction_count_1d_1d";
	private final String FEATURE = StringUtils.join(FEATURE_NAMESPACE,".",FEATURE_NAME);

	@Before
	public void setup() {
		TECTON_URL = System.getenv("TECTON_URL");
		TECTON_API_KEY = System.getenv("TECTON_API_KEY");
		WORKSPACE_NAME = System.getenv("WORKSPACE_NAME");
		FEATURE_SERVICE_NAME = System.getenv("FEATURE_SERVICE_NAME");
		tectonClient = new TectonClient(TECTON_URL, TECTON_API_KEY);
	}

	@Test
	public void testGetFeaturesE2E() {
		GetFeaturesRequestData getFeaturesRequestData =
				new GetFeaturesRequestData()
						.addJoinKey("user_id", "user_205125746682")
						.addJoinKey("merchant", "entertainment")
						.addRequestContext("amt", 500.00);
		GetFeaturesRequest getFeaturesRequest =
				new GetFeaturesRequest(WORKSPACE_NAME, FEATURE_SERVICE_NAME, getFeaturesRequestData);
		GetFeaturesResponse getFeaturesResponse = tectonClient.getFeatures(getFeaturesRequest);

		List<FeatureValue> featureValueList = getFeaturesResponse.getFeatureValues();
		Assert.assertFalse(featureValueList.isEmpty());

		Map<String, FeatureValue> featureValues = getFeaturesResponse.getFeatureValuesAsMap();
		FeatureValue sampleFeatureValue = featureValues.get(FEATURE);
		validateSampleFeature(sampleFeatureValue);
		tectonClient.close();
	}

	@Test
	public void testGetFeaturesBatchE2E() throws IOException {
		List<GetFeaturesRequestData> getFeaturesRequestDataList = GetFeaturesBatchDemo.generateFraudRequestDataFromFile("input.csv");
		GetFeaturesBatchRequest batchRequest = new GetFeaturesBatchRequest(WORKSPACE_NAME, FEATURE_SERVICE_NAME, getFeaturesRequestDataList, RequestConstants.DEFAULT_METADATA_OPTIONS, 5);
		GetFeaturesBatchResponse batchResponse = tectonClient.getFeaturesBatch(batchRequest);

		Assert.assertEquals(200, batchResponse.getBatchResponseList().size());
		GetFeaturesResponse sampleResponse = batchResponse.getBatchResponseList().get(0);
		FeatureValue featureValue = sampleResponse.getFeatureValuesAsMap().get(FEATURE);
		validateSampleFeature(featureValue);
	}

	@Test
	public void testGetFeaturesBatchE2E_ParallelRequests() throws IOException {
		TectonClientOptions clientOptions = new TectonClientOptions.Builder().maxParallelRequests(25).build();
		TectonClient tectonClient = new TectonClient(TECTON_URL, TECTON_API_KEY, clientOptions);
		List<GetFeaturesRequestData> getFeaturesRequestDataList = GetFeaturesBatchDemo.generateFraudRequestDataFromFile("input.csv");
		GetFeaturesBatchRequest batchRequest = new GetFeaturesBatchRequest(WORKSPACE_NAME, FEATURE_SERVICE_NAME, getFeaturesRequestDataList, RequestConstants.DEFAULT_METADATA_OPTIONS);
		GetFeaturesBatchResponse batchResponse = tectonClient.getFeaturesBatch(batchRequest);

		Assert.assertEquals(200, batchResponse.getBatchResponseList().size());
		GetFeaturesResponse sampleResponse = batchResponse.getBatchResponseList().get(0);
		FeatureValue featureValue = sampleResponse.getFeatureValuesAsMap().get(FEATURE);
		validateSampleFeature(featureValue);
	}

	@Test
	public void testServiceMetadataE2E() {
		GetFeatureServiceMetadataRequest request = new GetFeatureServiceMetadataRequest(FEATURE_SERVICE_NAME, WORKSPACE_NAME);
		GetFeatureServiceMetadataResponse response = tectonClient.getFeatureServiceMetadata(request);
		FeatureServiceMetadata featureServiceMetadata = response.getFeatureServiceMetadata();
		List<NameAndType> inputJoinKeys = featureServiceMetadata.getInputJoinKeys();
		List<NameAndType> requestContextKeys = featureServiceMetadata.getInputRequestContextKeys();
		List<NameAndType> featureMetadata = featureServiceMetadata.getFeatureValues();
		Assert.assertFalse(inputJoinKeys.isEmpty());
		Assert.assertFalse(requestContextKeys.isEmpty());
		Assert.assertFalse(featureMetadata.isEmpty());
	}

	private void validateSampleFeature(FeatureValue sampleFeatureValue) {
		Assert.assertEquals(FEATURE_NAMESPACE, sampleFeatureValue.getFeatureNamespace());
		Assert.assertEquals(FEATURE_NAME, sampleFeatureValue.getFeatureName());
		Assert.assertEquals(ValueType.INT64, sampleFeatureValue.getValueType());
	}

}
