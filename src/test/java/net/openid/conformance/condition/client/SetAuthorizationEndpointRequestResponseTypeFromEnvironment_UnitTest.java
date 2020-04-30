package net.openid.conformance.condition.client;

import static org.assertj.core.api.Assertions.assertThat;

import net.openid.conformance.testmodule.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;

import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.logging.TestInstanceEventLog;

@RunWith(MockitoJUnitRunner.class)
public class SetAuthorizationEndpointRequestResponseTypeFromEnvironment_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private SetAuthorizationEndpointRequestResponseTypeFromEnvironment cond;

	@Before
	public void setUp() throws Exception {
		cond = new SetAuthorizationEndpointRequestResponseTypeFromEnvironment();
		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);
	}

	@Test
	public void testEvaluate_code() {

		env.putObject("authorization_endpoint_request", new JsonObject());
		env.putString("response_type", "code");

		cond.execute(env);

		assertThat(env.getString("authorization_endpoint_request", "response_type")).isEqualTo("code");
	}

	@Test
	public void testEvaluate_idToken() {

		env.putObject("authorization_endpoint_request", new JsonObject());
		env.putString("response_type", "id_token");

		cond.execute(env);

		assertThat(env.getString("authorization_endpoint_request", "response_type")).isEqualTo("id_token");
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_missing() {

		env.putObject("authorization_endpoint_request", new JsonObject());

		cond.execute(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_empty() {

		env.putObject("authorization_endpoint_request", new JsonObject());
		env.putString("response_type", "");

		cond.execute(env);
	}
}
