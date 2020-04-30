package net.openid.conformance.condition.client;

import static org.assertj.core.api.Assertions.assertThat;

import net.openid.conformance.testmodule.OIDFJSON;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;

@RunWith(MockitoJUnitRunner.class)
public class ExtractAccessTokenFromTokenResponse_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private JsonObject tokenResponse;

	private ExtractAccessTokenFromTokenResponse cond;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		cond = new ExtractAccessTokenFromTokenResponse();

		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);

		// Example from RFC6750
		tokenResponse = new JsonParser().parse("{" +
			"\"access_token\":\"mF_9.B5f-4.1JqM\"," +
			"\"token_type\":\"Bearer\"," +
			"\"expires_in\":3600," +
			"\"refresh_token\":\"tGzv3JOkF0XG5Qx2TlKWIA\"" +
			"}").getAsJsonObject();

	}

	/**
	 * Test method for {@link ExtractAccessTokenFromTokenResponse#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_valuePresent() {

		env.putObject("token_endpoint_response", tokenResponse);

		cond.execute(env);

		verify(env, atLeastOnce()).getString("token_endpoint_response", "access_token");
		verify(env, atLeastOnce()).getString("token_endpoint_response", "token_type");

		assertThat(env.getObject("access_token")).isNotNull();
		assertThat(env.getString("access_token", "value")).isEqualTo(OIDFJSON.getString(tokenResponse.get("access_token")));
		assertThat(env.getString("access_token", "type")).isEqualTo(OIDFJSON.getString(tokenResponse.get("token_type")));

	}

	/**
	 * Test method for {@link ExtractAccessTokenFromTokenResponse#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_valueMissing() {

		env.putObject("token_endpoint_response", new JsonObject());

		cond.execute(env);

	}

}
