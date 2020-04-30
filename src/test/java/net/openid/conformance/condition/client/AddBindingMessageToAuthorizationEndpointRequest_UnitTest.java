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
public class AddBindingMessageToAuthorizationEndpointRequest_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private AddBindingMessageToAuthorizationEndpointRequest cond;

	@Before
	public void setUp() throws Exception {
		cond = new AddBindingMessageToAuthorizationEndpointRequest();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
	}

	@Test
	public void testEvaluate_presentBindingMessage() {

		JsonObject authorizationEndpointRequest = new JsonObject();

		env.putObject("authorization_endpoint_request", authorizationEndpointRequest);

		cond.execute(env);

		assertThat(env.getObject("authorization_endpoint_request").has("binding_message")).isTrue();

		assertThat(env.getString("authorization_endpoint_request", "binding_message")).isEqualTo("1234");

	}

}
