package net.openid.conformance.heart;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.AddClientAssertionToTokenEndpointRequest;
import net.openid.conformance.condition.client.CallTokenEndpoint;
import net.openid.conformance.condition.client.CheckForAccessTokenValue;
import net.openid.conformance.condition.client.CheckForScopesInTokenResponse;
import net.openid.conformance.condition.client.CheckHeartServerJwksFields;
import net.openid.conformance.condition.client.CheckIfTokenEndpointResponseError;
import net.openid.conformance.condition.client.CheckServerKeysIsValid;
import net.openid.conformance.condition.client.CreateClientAuthenticationAssertionClaims;
import net.openid.conformance.condition.client.CreateJwksUri;
import net.openid.conformance.condition.client.CreateTokenEndpointRequestForClientCredentialsGrant;
import net.openid.conformance.condition.client.EnsureNoRefreshToken;
import net.openid.conformance.condition.client.ExtractAccessTokenFromTokenResponse;
import net.openid.conformance.condition.client.ExtractJWKsFromStaticClientConfiguration;
import net.openid.conformance.condition.client.FetchServerKeys;
import net.openid.conformance.condition.client.GetDynamicServerConfiguration;
import net.openid.conformance.condition.client.GetStaticClientConfiguration;
import net.openid.conformance.condition.client.ParseAccessTokenAsJwt;
import net.openid.conformance.condition.client.SignClientAuthenticationAssertion;
import net.openid.conformance.condition.client.ValidateAccessTokenHeartClaims;
import net.openid.conformance.condition.client.ValidateAccessTokenSignature;
import net.openid.conformance.condition.client.ValidateClientJWKsPrivatePart;
import net.openid.conformance.condition.client.ValidateServerJWKs;
import net.openid.conformance.condition.common.CheckDistinctKeyIdValueInServerJWKs;
import net.openid.conformance.condition.common.CheckForKeyIdInServerJWKs;
import net.openid.conformance.condition.common.CheckHeartServerConfiguration;
import net.openid.conformance.condition.common.DisallowTLS10;
import net.openid.conformance.condition.common.DisallowTLS11;
import net.openid.conformance.condition.common.EnsureTLS12WithFAPICiphers;
import net.openid.conformance.condition.common.SetTLSTestHostFromConfig;
import net.openid.conformance.testmodule.AbstractTestModule;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.testmodule.TestFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@PublishTestModule(
	testName = "heart-direct-access-client",
	displayName = "HEART AS: Direct Access Client",
	profile = "HEART",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.jwks",
		"client.scope",
		"tls.testHost",
		"tls.testPort"
	}
)
public class DirectAccessClientAS extends AbstractTestModule {

	/* (non-Javadoc)
	 * @see io.bspk.selenium.TestModule#configure(com.google.gson.JsonObject)
	 */
	@Override
	public void configure(JsonObject config, String baseUrl, String externalUrlOverride) {
		env.putString("base_url", baseUrl);
		env.putObject("config", config);

		callAndStopOnFailure(SetTLSTestHostFromConfig.class);
		callAndStopOnFailure(EnsureTLS12WithFAPICiphers.class, "HEART-OAuth2-6");
		callAndContinueOnFailure(DisallowTLS10.class, "HEART-OAuth2-6");
		callAndContinueOnFailure(DisallowTLS11.class, "HEART-OAuth2-6");

		// Get the server's configuration
		callAndContinueOnFailure(GetDynamicServerConfiguration.class, "HEART-OAuth2-3.1.5");

		// make sure the server configuration passes some basic sanity checks
		callAndStopOnFailure(CheckHeartServerConfiguration.class, "HEART-OAuth2-3.1.5");

		// fetch or load the server's keys as needed
		callAndStopOnFailure(FetchServerKeys.class, "HEART-OAuth2-3.1.5");
		callAndContinueOnFailure(CheckServerKeysIsValid.class, Condition.ConditionResult.WARNING);
		callAndStopOnFailure(ValidateServerJWKs.class, "RFC7517-1.1", "RFC7517-1.1");
		callAndStopOnFailure(CheckHeartServerJwksFields.class, "HEART-OAuth2-3.1.5");
		callAndContinueOnFailure(CheckForKeyIdInServerJWKs.class, Condition.ConditionResult.FAILURE, "OIDCC-10.1");
		callAndContinueOnFailure(CheckDistinctKeyIdValueInServerJWKs.class, Condition.ConditionResult.FAILURE, "RFC7517-4.5");

		// Set up the client configuration
		callAndStopOnFailure(GetStaticClientConfiguration.class);

		callAndStopOnFailure(ValidateClientJWKsPrivatePart.class, "RFC7517-1.1");
		callAndStopOnFailure(ExtractJWKsFromStaticClientConfiguration.class, "HEART-OAuth2-2.1.5");

		callAndStopOnFailure(CreateJwksUri.class);
		exposeEnvString("jwks_uri");

		exposeEnvString("client_id");

		setStatus(Status.CONFIGURED);
		fireSetupDone();
	}

	/* (non-Javadoc)
	 * @see io.bspk.selenium.TestModule#start()
	 */
	@Override
	public void start() {

		setStatus(Status.RUNNING);

		callAndStopOnFailure(CreateTokenEndpointRequestForClientCredentialsGrant.class);

		// authenticate using a signed assertion
		callAndStopOnFailure(CreateClientAuthenticationAssertionClaims.class, "HEART-OAuth2-2.2.2");
		callAndStopOnFailure(SignClientAuthenticationAssertion.class, "HEART-OAuth2-2.2.2");
		callAndStopOnFailure(AddClientAssertionToTokenEndpointRequest.class, "HEART-OAuth2-2.2.2");

		callAndStopOnFailure(CallTokenEndpoint.class);

		callAndStopOnFailure(CheckIfTokenEndpointResponseError.class);

		callAndStopOnFailure(CheckForAccessTokenValue.class);

		callAndStopOnFailure(ExtractAccessTokenFromTokenResponse.class);

		callAndStopOnFailure(ParseAccessTokenAsJwt.class, "HEART-OAuth2-3.2.1");

		callAndStopOnFailure(ValidateAccessTokenSignature.class, "HEART-OAuth2-3.2.1");

		callAndContinueOnFailure(ValidateAccessTokenHeartClaims.class, Condition.ConditionResult.FAILURE, "HEART-OAuth2-3.2.1");

		callAndContinueOnFailure(CheckForScopesInTokenResponse.class);

		callAndStopOnFailure(EnsureNoRefreshToken.class);

		fireTestFinished();
	}

	@Override
	public Object handleHttp(String path, HttpServletRequest req, HttpServletResponse res, HttpSession session, JsonObject requestParts) {
		// dispatch based on the path
		if (path.equals("jwks")) {
			return handleJwks(requestParts);
		} else {
			throw new TestFailureException(getId(), "Got an HTTP response we weren't expecting");
		}

	}

	private Object handleJwks(JsonObject requestParts) {
		setStatus(Status.RUNNING);
		JsonObject jwks = env.getObject("server_public_jwks");

		setStatus(Status.WAITING);

		return new ResponseEntity<Object>(jwks, HttpStatus.OK);
	}

}
