package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.badRequest;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DisallowAccessTokenInQuery_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	// Examples from RFC 6749

	private static JsonObject bearerToken = new JsonParser().parse("{"
		+ "\"value\":\"mF_9.B5f-4.1JqM\","
		+ "\"type\":\"Bearer\""
		+ "}").getAsJsonObject();

	@ClassRule
	public static HoverflyRule hoverfly = HoverflyRule.inSimulationMode(dsl(
		service("good.example.com")
			.get("/accounts")
			.queryParam("access_token", any())
			.willReturn(badRequest().body("Bad Request")),
		service("bad.example.com")
			.get("/accounts")
			.queryParam("access_token", "mF_9.B5f-4.1JqM")
			.willReturn(success("OK", "text/plain"))));

	private DisallowAccessTokenInQuery cond;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		hoverfly.resetJournal();

		cond = new DisallowAccessTokenInQuery();

		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);

		env.putObject("resource", new JsonObject());
	}

	@Test
	public void testEvaluate_noError() {

		env.putObject("access_token", bearerToken);
		env.putString("protected_resource_url", "http://good.example.com/accounts");

		cond.execute(env);

		hoverfly.verify(service("good.example.com")
			.get("/accounts")
			.queryParam("access_token", "mF_9.B5f-4.1JqM"));

		verify(env, atLeastOnce()).getString("access_token", "value");
		verify(env, atLeastOnce()).getString("protected_resource_url");
	}

	/**
	 * Test method for {@link DisallowAccessTokenInQuery#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_disallowedQueryAccepted() {

		env.putObject("access_token", bearerToken);
		env.putString("protected_resource_url", "http://bad.example.com/accounts");

		cond.execute(env);

	}

	/**
	 * Test method for {@link DisallowAccessTokenInQuery#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_badServer() {

		env.putObject("access_token", bearerToken);
		env.putString("protected_resource_url", "http://invalid.org/accounts");

		cond.execute(env);

	}

	/**
	 * Test method for {@link DisallowAccessTokenInQuery#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingToken() {

		env.putString("protected_resource_url", "http://good.example.com/accounts");

		cond.execute(env);

	}

	/**
	 * Test method for {@link DisallowAccessTokenInQuery#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingUrl() {

		env.putObject("access_token", bearerToken);

		cond.execute(env);

	}

}
