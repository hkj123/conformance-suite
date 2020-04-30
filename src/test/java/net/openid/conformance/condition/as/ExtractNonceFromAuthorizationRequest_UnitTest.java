package net.openid.conformance.condition.as;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.junit.Assert.assertEquals;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;

@RunWith(MockitoJUnitRunner.class)
public class ExtractNonceFromAuthorizationRequest_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private ExtractNonceFromAuthorizationRequest cond;

	private String nonce = "123456";

	private JsonObject hasNonce;
	private JsonObject noNonce;
	private JsonObject onlyNonce;
	private JsonObject noParams;

	@Before
	public void setUp() throws Exception {

		cond = new ExtractNonceFromAuthorizationRequest();

		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);

		hasNonce = new JsonParser().parse("{\"nonce\": \"" + nonce + "\", \"state\": \"843192\"}").getAsJsonObject();
		noNonce = new JsonParser().parse("{\"state\": \"843192\"}").getAsJsonObject();
		onlyNonce = new JsonParser().parse("{\"nonce\": \"" + nonce + "\"}").getAsJsonObject();
		noParams = new JsonParser().parse("{}").getAsJsonObject();

	}

	@Test
	public void test_good() {

		env.putObject(CreateEffectiveAuthorizationRequestParameters.ENV_KEY, hasNonce);
		cond.execute(env);

		verify(env, atLeastOnce()).getObject(CreateEffectiveAuthorizationRequestParameters.ENV_KEY);
		verify(env, times(1)).putString("nonce", nonce);

		assertEquals(env.getString("nonce"), nonce);
	}

	@Test
	public void test_only() {

		env.putObject(CreateEffectiveAuthorizationRequestParameters.ENV_KEY, onlyNonce);
		cond.execute(env);

		verify(env, atLeastOnce()).getObject(CreateEffectiveAuthorizationRequestParameters.ENV_KEY);
		verify(env, times(1)).putString("nonce", nonce);

		assertEquals(env.getString("nonce"), nonce);

	}

	@Test(expected = ConditionError.class)
	public void test_bad() {

		env.putObject(CreateEffectiveAuthorizationRequestParameters.ENV_KEY, noNonce);
		cond.execute(env);

	}
	@Test(expected = ConditionError.class)
	public void test_missing() {

		env.putObject(CreateEffectiveAuthorizationRequestParameters.ENV_KEY, noParams);
		cond.execute(env);

	}
}
