package net.openid.conformance.condition.client;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class ValidateAccessTokenHeartClaims extends AbstractCondition {

	@PreEnvironment(required = {"access_token_jwt", "server", "client"})
	@Override
	public Environment evaluate(Environment env) {

		JsonElement el = env.getElementFromObject("access_token_jwt", "claims");

		if (el == null || !el.isJsonObject()) {
			throw error("Couldn't find access token JWT claims", args("access_token_jwt", env.getObject("access_token_jwt")));
		}

		JsonObject claims = el.getAsJsonObject();

		List<String> required = ImmutableList.of("iss", "azp", "exp", "jti");
		List<String> optional = ImmutableList.of("sub", "aud");

		Set<String> claimsFound = claims.keySet();

		if (!claimsFound.containsAll(required)) {
			throw error("Missing required claims in access token", args("expected", required, "actual", claimsFound));
		}

		String clientId = env.getString("client", "client_id");
		String azp = env.getString("access_token_jwt", "claims.azp");
		if (!clientId.equals(azp)) {
			throw error("azp claim was not client id", args("expected", clientId, "actual", azp));
		}

		String issuer = env.getString("server", "issuer");
		String iss = env.getString("access_token_jwt", "claims.iss");
		if (!issuer.equals(iss)) {
			throw error("iss claim was not issuer", args("expected", issuer, "actual", iss));
		}

		logSuccess("Found all required claims", args("required", required, "optional", optional, "actual", claimsFound));

		return env;

	}

}
