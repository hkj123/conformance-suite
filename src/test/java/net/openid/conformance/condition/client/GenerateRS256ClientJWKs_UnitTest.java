package net.openid.conformance.condition.client;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.nimbusds.jose.jwk.JWKSet;

import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;

@RunWith(MockitoJUnitRunner.class)
public class GenerateRS256ClientJWKs_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private GenerateRS256ClientJWKs cond;

	@Before
	public void setup() {
		cond = new GenerateRS256ClientJWKs();
		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);
	}

	private void checkJWKs(String key) throws ParseException {
		assertTrue(env.containsObject(key));
		JsonObject jwks = env.getObject(key);
		JWKSet keys = JWKSet.parse(jwks.toString());
		assertTrue(keys.getKeys().size() == 1);
	}

	@Test
	public void testExecute_noError() throws Exception {
		cond.execute(env);
		checkJWKs("client_jwks");
		checkJWKs("client_public_jwks");
	}
}
