package net.openid.conformance.condition.client;

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
public class CheckTokenEndpointHttpStatusForInvalidRequestOrInvalidClientError_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private CheckTokenEndpointHttpStatusForInvalidRequestOrInvalidClientError cond;

	@Before
	public void setUp() throws Exception {
		cond = new CheckTokenEndpointHttpStatusForInvalidRequestOrInvalidClientError();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
	}

	@Test
	public void testEvaluate_caseInvalidRequest() {
		env.putInteger("token_endpoint_response_http_status", 400);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_request\"}").getAsJsonObject());

		cond.execute(env);
	}

	@Test
	public void testEvaluate_caseInvalidClient() {
		env.putInteger("token_endpoint_response_http_status", 401);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_client\"}").getAsJsonObject());

		cond.execute(env);
	}

	@Test
	public void testEvaluate_caseInvalidClient400() {
		env.putInteger("token_endpoint_response_http_status", 400);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_client\"}").getAsJsonObject());

		cond.execute(env);
	}


	@Test(expected = ConditionError.class)
	public void testEvaluate_caseInvalidRequestObject() {
		env.putInteger("token_endpoint_response_http_status", 400);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_request_object\"}").getAsJsonObject());

		cond.execute(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseBadHttpStatusInvalidRequest() {
		env.putInteger("token_endpoint_response_http_status", 401);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_request\"}").getAsJsonObject());

		cond.execute(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseBadHttpStatusInvalidClient() {
		env.putInteger("token_endpoint_response_http_status", 402);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_client\"}").getAsJsonObject());

		cond.execute(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseBadHttpStatus200InvalidClient() {
		env.putInteger("token_endpoint_response_http_status", 200);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_client\"}").getAsJsonObject());

		cond.execute(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseBadHttpErrorInvalidRequestObject() {
		env.putInteger("token_endpoint_response_http_status", 401);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"invalid_request_object\"}").getAsJsonObject());

		cond.execute(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseBadErrorAccessDenied() {
		env.putInteger("token_endpoint_response_http_status", 401);
		env.putObject("token_endpoint_response", new JsonParser().parse("{\"error\":\"access_denied\"}").getAsJsonObject());

		cond.execute(env);
	}
}
