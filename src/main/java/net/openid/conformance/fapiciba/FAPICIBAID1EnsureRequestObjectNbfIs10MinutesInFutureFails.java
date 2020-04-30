package net.openid.conformance.fapiciba;

import net.openid.conformance.condition.client.AddNbfValueIs10MinutesInFutureToRequestObject;
import net.openid.conformance.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-ciba-id1-ensure-request-object-nbf-is-10-minutes-in-future-fails",
	displayName = "FAPI-CIBA-ID1: 'nbf' value in request object is 10 minutes in the future, should return an error",
	summary = "This test should return an error that the 'nbf' value in request object from back channel authentication endpoint request is 10 minutes in the future",
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
public class FAPICIBAID1EnsureRequestObjectNbfIs10MinutesInFutureFails extends AbstractFAPICIBAID1EnsureSendingInvalidBackchannelAuthorisationRequest {

	@Override
	protected void createAuthorizationRequestObject() {
		super.createAuthorizationRequestObject();
		callAndStopOnFailure(AddNbfValueIs10MinutesInFutureToRequestObject.class, "FAPI-CIBA-5.2.2-9");
	}

}
