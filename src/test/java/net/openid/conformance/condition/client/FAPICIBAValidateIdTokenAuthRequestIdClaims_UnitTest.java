package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FAPICIBAValidateIdTokenAuthRequestIdClaims_UnitTest {
	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private FAPICIBAValidateIdTokenAuthRequestIdClaims cond;

	@Before
	public void setUp() throws Exception {
		cond = new FAPICIBAValidateIdTokenAuthRequestIdClaims();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
	}

	@Test(expected = NullPointerException.class)
	public void testEvaluate_caseGoodEmpty() {
		JsonObject requestParameters = new JsonParser().parse("{\"auth_req_id\":\"FlFNzv_88I2U4ELEhI3-STEtd-DDQFVD-_UqfRKgxrE\"}").getAsJsonObject();
		env.putObject("token_endpoint_request_form_parameters", requestParameters);

		JsonObject claims = new JsonParser().parse("{\"claims\":{}}").getAsJsonObject();
		env.putObject("id_token", claims);

		cond.execute(env);

	}

	@Test
	public void testEvaluate_caseGood() {
		JsonObject requestParameters = new JsonParser().parse("{\"auth_req_id\":\"FlFNzv_88I2U4ELEhI3-STEtd-DDQFVD-_UqfRKgxrE\"}").getAsJsonObject();
		env.putObject("token_endpoint_request_form_parameters", requestParameters);

		JsonObject claims = new JsonParser().parse("{\"claims\":{\"urn:openid:params:jwt:claim:auth_req_id\": \"FlFNzv_88I2U4ELEhI3-STEtd-DDQFVD-_UqfRKgxrE\"}}").getAsJsonObject();
		env.putObject("id_token", claims);

		cond.execute(env);

	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseBad() {
		JsonObject requestParameters = new JsonParser().parse("{\"auth_req_id\":\"FlFNzv_88I2U4ELEhI3-STEtd-DDQFVD-_UqfRKgxrE\"}").getAsJsonObject();
		env.putObject("token_endpoint_request_form_parameters", requestParameters);

		JsonObject claims = new JsonParser().parse("{\"claims\":{\"urn:openid:params:jwt:claim:auth_req_id\": \"1c266114-a1be-4252-8ad1-04986c5b9ac1\"}}").getAsJsonObject();
		env.putObject("id_token", claims);

		cond.execute(env);

	}
}
