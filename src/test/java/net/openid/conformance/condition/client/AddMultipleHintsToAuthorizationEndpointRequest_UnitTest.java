package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AddMultipleHintsToAuthorizationEndpointRequest_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private AddMultipleHintsToAuthorizationEndpointRequest cond;

	@Before
	public void setUp() throws Exception {
		cond = new AddMultipleHintsToAuthorizationEndpointRequest();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
	}

	/**
	 * This case to test present two hints value in authorization endpoint request ('login_hint' and 'login_hint_token')
	 */
	@Test
	public void testEvaluate_presentTwoHints() {

		JsonObject authorizationEndpointRequest = new JsonObject();

		env.putObject("authorization_endpoint_request", authorizationEndpointRequest);

		cond.execute(env);

		assertThat(env.getObject("authorization_endpoint_request").has("login_hint")).isTrue();

		assertThat(env.getObject("authorization_endpoint_request").has("login_hint_token")).isTrue();

	}

}
