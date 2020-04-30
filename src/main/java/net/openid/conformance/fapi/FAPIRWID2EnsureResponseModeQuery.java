package net.openid.conformance.fapi;

import com.google.gson.JsonObject;

import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.CheckAuthorizationResponseWhenResponseModeQuery;
import net.openid.conformance.condition.client.CheckStateInAuthorizationResponse;
import net.openid.conformance.condition.client.EnsureErrorFromAuthorizationEndpointResponse;
import net.openid.conformance.condition.client.EnsureInvalidRequestError;
import net.openid.conformance.condition.client.ExpectResponseModeQueryErrorPage;
import net.openid.conformance.condition.client.RejectAuthCodeInUrlQuery;
import net.openid.conformance.condition.client.SetAuthorizationEndpointRequestResponseModeToQuery;
import net.openid.conformance.condition.client.CheckForUnexpectedParametersInErrorResponseFromAuthorizationEndpoint;
import net.openid.conformance.sequence.ConditionSequence;
import net.openid.conformance.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-rw-id2-ensure-response-mode-query",
	displayName = "FAPI-RW-ID2: ensure response_mode query",
	summary = "This test includes response_mode=query in the authorization request. The authorization server should show an error message that response_mode=query is not allowed by FAPI-RW (a screenshot of which should be uploaded), should return an error to the client, or must successfully authenticate without returning the result in the query.",
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
public class FAPIRWID2EnsureResponseModeQuery extends AbstractFAPIRWID2ExpectingAuthorizationEndpointPlaceholderOrCallback {

	@Override
	protected void createPlaceholder() {
		callAndStopOnFailure(ExpectResponseModeQueryErrorPage.class, "OAuth2-RT-5");

		env.putString("error_callback_placeholder", env.getString("response_mode_error"));
	}

	@Override
	protected ConditionSequence makeCreateAuthorizationRequestSteps() {

		return super.makeCreateAuthorizationRequestSteps()
				.then(condition(SetAuthorizationEndpointRequestResponseModeToQuery.class));
	}

	@Override
	protected void processCallback() {

		callAndContinueOnFailure(RejectAuthCodeInUrlQuery.class, Condition.ConditionResult.FAILURE, "OIDCC-3.3.2.5");

		// This call will map authorization_endpoint_response onto callback_query_params or callback_params depending
		// what response the server decided to return and where
		callAndStopOnFailure(CheckAuthorizationResponseWhenResponseModeQuery.class, Condition.ConditionResult.FAILURE, "OAuth2-RT-5");

		JsonObject authorizationEndpointResponse = env.getObject("authorization_endpoint_response");

		if (authorizationEndpointResponse.has("error")) {

			callAndContinueOnFailure(CheckStateInAuthorizationResponse.class, Condition.ConditionResult.FAILURE);

			callAndContinueOnFailure(EnsureErrorFromAuthorizationEndpointResponse.class, Condition.ConditionResult.FAILURE, "OIDCC-3.1.2.6");

			callAndContinueOnFailure(CheckForUnexpectedParametersInErrorResponseFromAuthorizationEndpoint.class, Condition.ConditionResult.WARNING, "OIDCC-3.1.2.6");

			callAndContinueOnFailure(EnsureInvalidRequestError.class, Condition.ConditionResult.FAILURE, "OIDCC-3.3.2.6");

		}

		fireTestFinished();
	}
}
