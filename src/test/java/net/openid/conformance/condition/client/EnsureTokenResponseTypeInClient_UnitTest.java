package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EnsureTokenResponseTypeInClient_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private EnsureTokenResponseTypeInClient cond;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		cond = new EnsureTokenResponseTypeInClient();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
	}

	/**
	 * Test method for {@link EnsureTokenResponseTypeInClient#evaluate(Environment)}.
	 */
	@Test
	public void testEvalutate_properResponseCode(){
		JsonObject goodRespone = new JsonParser().parse("{" +
			"\"client_id\":\"UNIT-TEST_Client_Id\"," +
			"\"client_secret\":\"secret\"," +
			"\"client_secret_expires_at\":0," +
			"\"client_id_issued_at\":1525197719," +
			"\"registration_access_token\":\"reg.access.token\"," +
			"\"registration_client_uri\":\"https://example.org/register/UNIT-TEST_Client_Id\"," +
			"\"redirect_uris\":[\"https://localhost:8443/test/a/unit-test/callback\"]," +
			"\"client_name\":\"unit-test-client\"," +
			"\"token_endpoint_auth_method\":\"client_secret_basic\"," +
			"\"scope\":\"openid email profile\"," +
			"\"grant_types\":[\"implicit\"]," +
			"\"response_types\":[\"token\"]}").getAsJsonObject();

		env.putObject("client", goodRespone);
		cond.execute(env);
	}

	/**
	 * Test method for {@link EnsureTokenResponseTypeInClient#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvalutate_wrongResponseCode(){
		JsonObject goodRespone = new JsonParser().parse("{" +
			"\"client_id\":\"UNIT-TEST_Client_Id\"," +
			"\"client_secret\":\"secret\"," +
			"\"client_secret_expires_at\":0," +
			"\"client_id_issued_at\":1525197719," +
			"\"registration_access_token\":\"reg.access.token\"," +
			"\"registration_client_uri\":\"https://example.org/register/UNIT-TEST_Client_Id\"," +
			"\"redirect_uris\":[\"https://localhost:8443/test/a/unit-test/callback\"]," +
			"\"client_name\":\"unit-test-client\"," +
			"\"token_endpoint_auth_method\":\"client_secret_basic\"," +
			"\"scope\":\"openid email profile\"," +
			"\"grant_types\":[\"implicit\"]," +
			"\"response_types\":[\"code\"]}").getAsJsonObject();

		env.putObject("client", goodRespone);
		cond.execute(env);
	}

	/**
	 * Test method for {@link EnsureTokenResponseTypeInClient#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvalutate_noResponseCode(){
		JsonObject goodRespone = new JsonParser().parse("{" +
			"\"client_id\":\"UNIT-TEST_Client_Id\"," +
			"\"client_secret\":\"secret\"," +
			"\"client_secret_expires_at\":0," +
			"\"client_id_issued_at\":1525197719," +
			"\"registration_access_token\":\"reg.access.token\"," +
			"\"registration_client_uri\":\"https://example.org/register/UNIT-TEST_Client_Id\"," +
			"\"redirect_uris\":[\"https://localhost:8443/test/a/unit-test/callback\"]," +
			"\"client_name\":\"unit-test-client\"," +
			"\"token_endpoint_auth_method\":\"client_secret_basic\"," +
			"\"scope\":\"openid email profile\"," +
			"\"grant_types\":[\"implicit\"]}").getAsJsonObject();

		env.putObject("client", goodRespone);
		cond.execute(env);
	}

	/**
	 * Test method for {@link EnsureTokenResponseTypeInClient#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void testEvalutate_tooManyResponseCodes(){
		JsonObject goodRespone = new JsonParser().parse("{" +
			"\"client_id\":\"UNIT-TEST_Client_Id\"," +
			"\"client_secret\":\"secret\"," +
			"\"client_secret_expires_at\":0," +
			"\"client_id_issued_at\":1525197719," +
			"\"registration_access_token\":\"reg.access.token\"," +
			"\"registration_client_uri\":\"https://example.org/register/UNIT-TEST_Client_Id\"," +
			"\"redirect_uris\":[\"https://localhost:8443/test/a/unit-test/callback\"]," +
			"\"client_name\":\"unit-test-client\"," +
			"\"token_endpoint_auth_method\":\"client_secret_basic\"," +
			"\"scope\":\"openid email profile\"," +
			"\"grant_types\":[\"implicit\"]," +
			"\"response_types\":[\"token\", \"code\"]}").getAsJsonObject();

		env.putObject("client", goodRespone);
		cond.execute(env);
	}

	/**
	 * Test method for {@link EnsureTokenResponseTypeInClient#evaluate(Environment)}.
	 */
	@Test(expected = ConditionError.class)
	public void tooManyEvalutate_emptyArrayResponseCode(){
		JsonObject goodRespone = new JsonParser().parse("{" +
			"\"client_id\":\"UNIT-TEST_Client_Id\"," +
			"\"client_secret\":\"secret\"," +
			"\"client_secret_expires_at\":0," +
			"\"client_id_issued_at\":1525197719," +
			"\"registration_access_token\":\"reg.access.token\"," +
			"\"registration_client_uri\":\"https://example.org/register/UNIT-TEST_Client_Id\"," +
			"\"redirect_uris\":[\"https://localhost:8443/test/a/unit-test/callback\"]," +
			"\"client_name\":\"unit-test-client\"," +
			"\"token_endpoint_auth_method\":\"client_secret_basic\"," +
			"\"scope\":\"openid email profile\"," +
			"\"grant_types\":[\"implicit\"]," +
			"\"response_types\":[]}").getAsJsonObject();

		env.putObject("client", goodRespone);
		cond.execute(env);
	}
}
