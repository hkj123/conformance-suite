package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.testmodule.OIDFJSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AddWrongAudToClientAssertionClaims_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private AddWrongAudToClientAssertionClaims cond;

	@Before
	public void setUp() throws Exception {
		cond = new AddWrongAudToClientAssertionClaims();
		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);
	}

	@Test
	public void testEvaluate() {

		JsonObject clientAssertClaims = new JsonObject();

		clientAssertClaims.addProperty("jti", RandomStringUtils.randomAlphanumeric(20));

		env.putObject("client_assertion_claims", clientAssertClaims);

		cond.execute(env);

		assertThat(env.getObject("client_assertion_claims")).isNotNull();

		JsonObject claims = env.getObject("client_assertion_claims");

		assertThat(OIDFJSON.getString(claims.get("aud"))).isEqualTo("https://fapidev-rs.authlete.net/api/userinfo");

	}
}
