package net.openid.conformance.condition.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;

@RunWith(MockitoJUnitRunner.class)
public class CreateS256CodeChallenge_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private CreateS256CodeChallenge cond;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		cond = new CreateS256CodeChallenge();
		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);
	}

	@Test(expected = ConditionError.class)
	public void testMissing() {
		cond.execute(env);
	}

	@Test
	public void testEvaluate() throws NoSuchAlgorithmException {

		String codeVerifier = "code verifier!";

		env.putString("code_verifier", codeVerifier);

		cond.execute(env);

		assertThat(env.getString("code_challenge_method")).isEqualTo("S256");

		String res = env.getString("code_challenge");

		assertThat(res).isNotEmpty();

		byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(bytes, 0, bytes.length);
		byte[] digest = md.digest();
		String challenge = Base64.encodeBase64URLSafeString(digest);

		assertThat(res).isEqualTo(challenge);

	}
}
