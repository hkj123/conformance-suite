package net.openid.conformance.openid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.gson.JsonObject;

import net.openid.conformance.condition.client.AddJwksUriToDynamicRegistrationRequest;
import net.openid.conformance.condition.client.AddPublicJwksToDynamicRegistrationRequest;
import net.openid.conformance.condition.client.CreateJwksUri;
import net.openid.conformance.condition.client.GenerateRS256ClientJWKs;
import net.openid.conformance.condition.client.GenerateRS256ClientJWKsWithKeyID;
import net.openid.conformance.sequence.client.OIDCCCreateDynamicClientRegistrationRequest;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.variant.ClientAuthType;
import net.openid.conformance.variant.ClientRegistration;
import net.openid.conformance.variant.ResponseType;
import net.openid.conformance.variant.VariantNotApplicable;

// Corresponds to https://www.heenan.me.uk/~joseph/oidcc_test_desc-phase1.html#OP_Registration_jwks_uri
@PublishTestModule(
	testName = "oidcc-registration-jwks-uri",
	displayName = "OIDCC: dynamic registration with JWKS URI",
	summary = "This test calls the dynamic registration endpoint with a jwks URI, and continues with authorization. This should result in a successful registration and authorization.",
	profile = "OIDCC",
	configurationFields = {
		"server.discoveryUrl"
	}
)
@VariantNotApplicable(parameter = ClientAuthType.class, values = {"client_secret_basic", "client_secret_post", "client_secret_jwt", "mtls"})
@VariantNotApplicable(parameter = ResponseType.class, values = {"id_token", "id_token token"})
@VariantNotApplicable(parameter = ClientRegistration.class, values = {"static_client"})
public class OIDCCRegistrationJwksUri extends AbstractOIDCCServerTest {

	@Override
	protected void createDynamicClientRegistrationRequest() {
		callAndStopOnFailure(CreateJwksUri.class);
		call(new OIDCCCreateDynamicClientRegistrationRequest(responseType)
				.replace(GenerateRS256ClientJWKs.class,
						condition(GenerateRS256ClientJWKsWithKeyID.class))
				.replace(AddPublicJwksToDynamicRegistrationRequest.class,
						condition(AddJwksUriToDynamicRegistrationRequest.class)));

		expose("client_name", env.getString("dynamic_registration_request", "client_name"));
	}

	@Override
	public Object handleHttp(String path, HttpServletRequest req, HttpServletResponse res, HttpSession session, JsonObject requestParts) {
		if (path.equals("jwks")) {
			return handleJwksRequest(requestParts);
		} else {
			return super.handleHttp(path, req, res, session, requestParts);
		}
	}

	private Object handleJwksRequest(JsonObject requestParts) {
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(env.getObject("client_public_jwks"));
	}
}
