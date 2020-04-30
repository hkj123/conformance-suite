package net.openid.conformance.condition.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.testmodule.OIDFJSON;

public class FAPICheckKeyAlgInClientJWKs extends AbstractCondition {

	@Override
	@PreEnvironment(required = "client_jwks")
	public Environment evaluate(Environment env) {
		JsonElement keys = env.getElementFromObject("client_jwks", "keys");
		if (keys == null || !keys.isJsonArray()) {
			throw error("keys array not found in JWKs");
		}

		boolean found = false;
		for (JsonElement key : keys.getAsJsonArray()) {
			if (!key.isJsonObject()) {
				throw error("invalid key in JWKs", args("key", key));
			}
			JsonObject keyObj = key.getAsJsonObject();

			if (!keyObj.has("alg")) {
				throw error("'alg' not found in client JWKS provided in the test configuration - this is "+
						"required to set the request object signing algorithm the conformance suite will use, and "+
						"should be set to PS256 or ES256",
					args("key", key));
			}

			String alg = OIDFJSON.getString(keyObj.getAsJsonPrimitive("alg"));
			if (alg.equals("PS256") || alg.equals("ES256")) {
				found = true;
			}
		}

		if (!found) {
			throw error("client jwks key should have alg PS256 or ES256", args("keys", keys));
		}

		logSuccess("Found a key with alg PS256 or ES256");

		return env;
	}

}
