package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
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
public class EnsureErrorFromBackchannelAuthenticationEndpointResponse_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private EnsureErrorFromBackchannelAuthenticationEndpointResponse cond;

	@Before
	public void setUp() throws Exception {
		cond = new EnsureErrorFromBackchannelAuthenticationEndpointResponse();

		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);

		JsonObject backChannelAuthenticationEndpointResponse = new JsonObject();

		backChannelAuthenticationEndpointResponse.addProperty("error", "invalid_request");

		backChannelAuthenticationEndpointResponse.addProperty("error_description", "[A167307] Failed to find a client application whose ID matches the value of the 'iss' claim in the request object included in the backchannel authentication request.");

		backChannelAuthenticationEndpointResponse.addProperty("error_uri", "https://www.authlete.com/documents/apis/result_codes#A167307");

		env.putObject("backchannel_authentication_endpoint_response", backChannelAuthenticationEndpointResponse);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_notExistErrorField() {

		JsonObject backChannelAuthenticationEndpointResponse = env.getObject("backchannel_authentication_endpoint_response");

		backChannelAuthenticationEndpointResponse.remove("error");

		env.putObject("backchannel_authentication_endpoint_response", backChannelAuthenticationEndpointResponse);

		cond.execute(env);
	}

	@Test
	public void testEvaluate_success() {
		cond.execute(env);
	}

}
