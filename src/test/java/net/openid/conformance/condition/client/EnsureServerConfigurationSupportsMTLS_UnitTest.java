package net.openid.conformance.condition.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;

@RunWith(MockitoJUnitRunner.class)
public class EnsureServerConfigurationSupportsMTLS_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private EnsureServerConfigurationSupportsMTLS cond;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		cond = new EnsureServerConfigurationSupportsMTLS();

		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);

		env.putObject("server", new JsonObject());
	}

	/**
	 * Test method for {@link EnsureServerConfigurationSupportsMTLS#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_noError() {

		JsonArray methods = new JsonArray();
		methods.add("client_secret_basic");
		methods.add("tls_client_auth");
		methods.add("self_signed_tls_client_auth");

		env.getObject("server").add("token_endpoint_auth_methods_supported", methods);

		cond.execute(env);

		verify(env, atLeastOnce()).getElementFromObject("server", "token_endpoint_auth_methods_supported");
	}

	/**
	 * Test method for {@link EnsureServerConfigurationSupportsMTLS#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_supportsPKIOnly() {

		JsonArray methods = new JsonArray();
		methods.add("tls_client_auth");

		env.getObject("server").add("token_endpoint_auth_methods_supported", methods);

		cond.execute(env);
	}

	/**
	 * Test method for {@link EnsureServerConfigurationSupportsMTLS#evaluate(Environment)}.
	 */
	@Test
	public void testEvaluate_supportsPublicKeyOnly() {

		JsonArray methods = new JsonArray();
		methods.add("self_signed_tls_client_auth");

		env.getObject("server").add("token_endpoint_auth_methods_supported", methods);

		cond.execute(env);
	}

	/**
	 * Test method for {@link EnsureServerConfigurationSupportsMTLS#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_supportsBasicOnly() {

		JsonArray methods = new JsonArray();
		methods.add("client_secret_basic");

		env.getObject("server").add("token_endpoint_auth_methods_supported", methods);

		cond.execute(env);
	}

	/**
	 * Test method for {@link EnsureServerConfigurationSupportsMTLS#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_defaultOnly() {

		cond.execute(env);
	}

}
