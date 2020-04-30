package net.openid.conformance.fapi;

import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.AddAudToRequestObject;
import net.openid.conformance.condition.client.AddExpToRequestObject;
import net.openid.conformance.condition.client.AddIssToRequestObject;
import net.openid.conformance.condition.client.BuildRequestObjectByValueRedirectToAuthorizationEndpoint;
import net.openid.conformance.condition.client.CheckIfAuthorizationEndpointError;
import net.openid.conformance.condition.client.CheckMatchingCallbackParameters;
import net.openid.conformance.condition.client.ConvertAuthorizationEndpointRequestToRequestObject;
import net.openid.conformance.condition.client.EnsureMinimumAuthorizationCodeEntropy;
import net.openid.conformance.condition.client.EnsureMinimumAuthorizationCodeLength;
import net.openid.conformance.condition.client.ExtractAuthorizationCodeFromAuthorizationResponse;
import net.openid.conformance.condition.client.SignRequestObject;
import net.openid.conformance.condition.client.VerifyNoStateInAuthorizationResponse;
import net.openid.conformance.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-rw-id2-ensure-authorization-request-without-state-success",
	displayName = "FAPI-RW-ID2: ensure authorization endpoint request without state success",
	summary = "This test makes an authentication request that does not include 'state'. State is an optional parameter, so the authorisation server must successfully authenticate and must not return state nor s_hash.",
	profile = "FAPI-RW-ID2",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.scope",
		"client.jwks",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.client_id",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl",
		"resource.institution_id"
	}
)
public class FAPIRWID2EnsureAuthorizationRequestWithoutStateSuccess extends AbstractFAPIRWID2EnsureRequestObjectWithoutState {

	@Override
	protected void performAuthorizationFlow() {
		performPreAuthorizationSteps();

		eventLog.startBlock(currentClientString() + "Make request to authorization endpoint");

		createAuthorizationRequest();

		createAuthorizationRedirect();

		performRedirect();
	}

	@Override
	protected void createAuthorizationRedirect() {
		callAndStopOnFailure(ConvertAuthorizationEndpointRequestToRequestObject.class);

		callAndStopOnFailure(AddExpToRequestObject.class);

		callAndStopOnFailure(AddAudToRequestObject.class);

		callAndStopOnFailure(AddIssToRequestObject.class);

		callAndStopOnFailure(SignRequestObject.class);

		callAndStopOnFailure(BuildRequestObjectByValueRedirectToAuthorizationEndpoint.class);
	}

	@Override
	protected void onAuthorizationCallbackResponse() {
		callAndStopOnFailure(CheckMatchingCallbackParameters.class);

		callAndStopOnFailure(CheckIfAuthorizationEndpointError.class);

		callAndContinueOnFailure(VerifyNoStateInAuthorizationResponse.class, Condition.ConditionResult.FAILURE);

		callAndStopOnFailure(ExtractAuthorizationCodeFromAuthorizationResponse.class);

		callAndContinueOnFailure(EnsureMinimumAuthorizationCodeLength.class, Condition.ConditionResult.FAILURE, "RFC6749-10.10");

		callAndContinueOnFailure(EnsureMinimumAuthorizationCodeEntropy.class, Condition.ConditionResult.FAILURE, "RFC6749-10.10");

		handleSuccessfulAuthorizationEndpointResponse();
	}
}
