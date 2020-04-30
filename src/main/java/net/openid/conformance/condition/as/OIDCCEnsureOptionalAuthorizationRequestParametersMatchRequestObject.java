package net.openid.conformance.condition.as;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Use this condition for logging a WARNING only
 * Normally this condition should never lead to a failure
 */
public class OIDCCEnsureOptionalAuthorizationRequestParametersMatchRequestObject extends AbstractCondition {

	@Override
	@PreEnvironment(required = {"authorization_endpoint_http_request_params", "authorization_request_object"})
	public Environment evaluate(Environment env) {

		//we loop over all parameters and add a log entry if they are not equal
		JsonObject authzEndpointReqParams = env.getObject("authorization_endpoint_http_request_params");
		JsonObject requestObjectClaims = env.getElementFromObject("authorization_request_object", "claims").getAsJsonObject();

		Map<String, Object> argsForLog = new HashMap<>();

		for (String paramName : authzEndpointReqParams.keySet()) {
			if(OIDCCEnsureRequiredAuthorizationRequestParametersMatchRequestObject.parametersThatMustMatch.contains(paramName)) {
				//these should be already checked. checking again would cause duplicate logs
				continue;
			}
			if(requestObjectClaims.has(paramName)) {
				//scope=openid special case. We don't log a warning when scope http request parameter equals openid,
				//only when it is exactly "openid".
				//We will log a warning when it is "openid xyz" in http request but "openid xyz abc" in request object
				if("scope".equals(paramName)) {
					String scopeValue = authzEndpointReqParams.get(paramName).getAsString();
					if("openid".equals(scopeValue)) {
						continue;
					}
				}
				if(!authzEndpointReqParams.get(paramName).equals(requestObjectClaims.get(paramName))) {
					argsForLog.put(paramName, args("Value in http request", authzEndpointReqParams.get(paramName),
													"Value in request object", requestObjectClaims.get(paramName))
					);
				}
			}
		}

		if(argsForLog.isEmpty()) {
			logSuccess("All http request parameters and request object claims match");
			return env;
		}
		throw error("Some http request parameters and request object claims do not match. " +
			"This is allowed but you should check if the differences are intentional", argsForLog);
	}

}
