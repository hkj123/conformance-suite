package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class ValidateServerJWKs extends AbstractValidateJWKs {

	@Override
	@PreEnvironment(required = "server_jwks")
	public Environment evaluate(Environment env) {
		JsonObject jwks = env.getObject("server_jwks");

		checkJWKs(jwks, false);

		logSuccess("Valid server JWKs");

		return env;
	}
}
