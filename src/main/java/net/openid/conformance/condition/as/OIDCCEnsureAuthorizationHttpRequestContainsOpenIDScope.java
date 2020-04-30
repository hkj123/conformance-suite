package net.openid.conformance.condition.as;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OIDCC 6.1 says:
 * Even if a scope parameter is present in the Request Object value, a scope parameter
 * MUST always be passed using the OAuth 2.0 request syntax containing the openid scope
 * value to indicate to the underlying OAuth 2.0 logic that this is an OpenID Connect request.
 */
public class OIDCCEnsureAuthorizationHttpRequestContainsOpenIDScope extends AbstractCondition {

	@Override
	@PreEnvironment(required = {"authorization_endpoint_http_request_params"})
	public Environment evaluate(Environment env) {
		String scopeFromHttpRequest = env.getString("authorization_endpoint_http_request_params", "scope");

		if(Strings.isNullOrEmpty(scopeFromHttpRequest)) {
			throw error("Http request parameters don't contain a scope parameter",
						args("request_parameters", env.getObject("authorization_endpoint_http_request_params")));
		}
		List<String> scopes = Lists.newArrayList(Splitter.on(" ").split(scopeFromHttpRequest).iterator());

		if (scopes.contains("openid")) {
			logSuccess("Found 'openid' in scope http request parameter", args("expected", "openid", "actual", scopes));
			return env;
		} else {
			throw error("Could not find 'openid' in scope http request parameter", args("expected", "openid", "actual", scopes));
		}
	}

}
