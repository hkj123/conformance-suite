package net.openid.conformance.fapiciba;

import net.openid.conformance.condition.client.RemoveIssFromRequestObject;
import net.openid.conformance.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-ciba-id1-ensure-request-object-missing-iss-fails",
	displayName = "FAPI-CIBA-ID1: Missing 'iss' value in request object, should return an error",
	summary = "This test should return an error that the 'iss' value in request object from back channel authentication endpoint request is missing",
	profile = "FAPI-CIBA-ID1",
	configurationFields = {
		"server.discoveryUrl",
		"client.scope",
		"client.jwks",
		"client.hint_type",
		"client.hint_value",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl"
	}
)
public class FAPICIBAID1EnsureRequestObjectMissingIssFails extends AbstractFAPICIBAID1EnsureSendingInvalidBackchannelAuthorisationRequest {

	@Override
	protected void createAuthorizationRequestObject() {
		super.createAuthorizationRequestObject();
		callAndStopOnFailure(RemoveIssFromRequestObject.class, "CIBA-7.1.1");
	}
}
