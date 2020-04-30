package net.openid.conformance.condition.client;

import com.google.gson.JsonElement;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class ValidateClientJWKsPrivatePart extends AbstractValidateJWKs {

	@Override
	@PreEnvironment(required = "client")
	public Environment evaluate(Environment env) {
		JsonElement jwks = env.getElementFromObject("client", "jwks");

		checkJWKs(jwks, true);

		logSuccess("Valid client JWKs");

		return env;
	}
}
