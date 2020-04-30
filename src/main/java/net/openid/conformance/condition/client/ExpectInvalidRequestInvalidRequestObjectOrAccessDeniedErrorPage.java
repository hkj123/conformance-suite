package net.openid.conformance.condition.client;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.testmodule.Environment;

public class ExpectInvalidRequestInvalidRequestObjectOrAccessDeniedErrorPage extends AbstractCondition {

	@Override
	@PostEnvironment(strings = "request_unverifiable_error")
	public Environment evaluate(Environment env) {

		String placeholder = createBrowserInteractionPlaceholder("If the server does not return an invalid_request or invalid_request_object or access_denied error back to the client, it must show an error page saying the request is invalid as it is missing the request_object or invalid_request_object or access_denied - upload a screenshot of the error page.");
		env.putString("request_unverifiable_error", placeholder);

		return env;
	}
}
