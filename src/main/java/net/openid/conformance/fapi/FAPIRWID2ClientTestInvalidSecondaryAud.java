package net.openid.conformance.fapi;

import net.openid.conformance.condition.as.AddSecondAudValueToIdToken;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.testmodule.TestFailureException;

@PublishTestModule(
	testName = "fapi-rw-id2-client-test-invalid-secondary-aud",
	displayName = "FAPI-RW-ID2: client test - multiple aud values in id_token from authorization_endpoint, should be rejected",
	summary = "This test should end with the client displaying an error message that there are multiple aud values in the id_token from the authorization_endpoint, and this behaviour is not expected",
	profile = "FAPI-RW-ID2",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	}
)

public class FAPIRWID2ClientTestInvalidSecondaryAud extends AbstractFAPIRWID2ClientExpectNothingAfterAuthorizationEndpoint {

	@Override
	protected void addCustomValuesToIdToken() {

		callAndStopOnFailure(AddSecondAudValueToIdToken.class, "OIDCC-3.1.3.7-8");
	}

	@Override
	protected Object authorizationCodeGrantType(String requestId) {

		throw new TestFailureException(getId(), "Client has incorrectly called token_endpoint after receiving an id_token with multiple aud values from the authorization_endpoint.");

	}

}
