package net.openid.conformance.condition.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;

@RunWith(MockitoJUnitRunner.class)
public class GetStaticServerConfiguration_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private JsonObject server;

	private JsonObject goodConfig;

	private JsonObject badConfig_notObject;

	private JsonObject badConfig_serverMissing;

	private GetStaticServerConfiguration cond;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		cond = new GetStaticServerConfiguration();

		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);

		server = new JsonParser().parse("{"
			+ "\"authorization_endpoint\":\"https://example.com/oauth/authorize\","
			+ "\"token_endpoint\":\"https://example.com/api/oauth/token\","
			+ "\"issuer\":\"ExampleApp\""
			+ "}").getAsJsonObject();

		goodConfig = new JsonObject();
		goodConfig.add("server", server);

		badConfig_notObject = new JsonObject();
		badConfig_notObject.addProperty("server", "this is a string");

		badConfig_serverMissing = new JsonObject();
	}

	/**
	 * Test method for {@link GetStaticServerConfiguration#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_valuePresent() {

		env.putObject("config", goodConfig);

		cond.execute(env);

		assertThat(env.getObject("server")).isEqualTo(server);
	}

	/**
	 * Test method for {@link GetStaticServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_serverNotObject() {

		env.putObject("config", badConfig_notObject);

		cond.execute(env);
	}

	/**
	 * Test method for {@link GetStaticServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_serverMissing() {

		env.putObject("config", badConfig_serverMissing);

		cond.execute(env);
	}

	/**
	 * Test method for {@link GetStaticServerConfiguration#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_configMissing() {

		cond.execute(env);
	}
}
