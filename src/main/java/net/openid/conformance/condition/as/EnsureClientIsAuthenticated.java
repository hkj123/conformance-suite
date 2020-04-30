package net.openid.conformance.condition.as;

import com.google.common.base.Strings;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.testmodule.Environment;

public class EnsureClientIsAuthenticated extends AbstractCondition {

	@Override
	// this doesn't use the @PreEnvironment check so that we can have a more specific error message below
	public Environment evaluate(Environment env) {

		if (Strings.isNullOrEmpty(env.getString("client_authentication_success"))) {
			throw error("Client was not authenticated");
		} else {
			logSuccess("Found client authentication, passing", args("client_authentication_success", env.getString("client_authentication_success")));

			return env;
		}

	}

}
