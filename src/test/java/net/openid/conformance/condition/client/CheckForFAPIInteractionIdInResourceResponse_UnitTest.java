package net.openid.conformance.condition.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;

@RunWith(MockitoJUnitRunner.class)
public class CheckForFAPIInteractionIdInResourceResponse_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private CheckForFAPIInteractionIdInResourceResponse cond;

	@Before
	public void setUp() throws Exception {
		cond = new CheckForFAPIInteractionIdInResourceResponse();
		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);
	}

	/**
	 * Test method for {@link CheckForFAPIInteractionIdInResourceResponse#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_noError() {

		JsonObject headers = new JsonObject();
		headers.addProperty("x-fapi-interaction-id", "c770aef3-6784-41f7-8e0e-ff5f97bddb3a"); // Example from FAPI 1
		env.putObject("resource_endpoint_response_headers", headers);

		cond.execute(env);

		verify(env, atLeastOnce()).getString("resource_endpoint_response_headers", "x-fapi-interaction-id");
	}

	/**
	 * Test method for {@link CheckForFAPIInteractionIdInResourceResponse#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_invalidValue() {

		JsonObject headers = new JsonObject();
		headers.addProperty("x-fapi-interaction-id", "this is not a uuid");
		env.putObject("resource_endpoint_response_headers", headers);

		cond.execute(env);
	}

	/**
	 * Test method for {@link CheckForFAPIInteractionIdInResourceResponse#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_missingValue() {

		JsonObject headers = new JsonObject();
		env.putObject("resource_endpoint_response_headers", headers);

		cond.execute(env);
	}

}
