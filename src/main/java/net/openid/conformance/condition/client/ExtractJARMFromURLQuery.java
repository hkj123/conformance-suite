package net.openid.conformance.condition.client;

import net.openid.conformance.condition.AbstractExtractJWT;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class ExtractJARMFromURLQuery extends AbstractExtractJWT {

	@Override
	@PreEnvironment(required = "callback_query_params")
	@PostEnvironment(required = "jarm_response")
	public Environment evaluate(Environment env) {

		return extractJWT(env, "callback_query_params", "response", "jarm_response");

	}

}
