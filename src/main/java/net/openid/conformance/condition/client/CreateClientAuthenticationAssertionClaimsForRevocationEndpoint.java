package net.openid.conformance.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;

public class CreateClientAuthenticationAssertionClaimsForRevocationEndpoint extends AbstractCondition {

	@Override
	@PreEnvironment(required = { "client", "server" })
	@PostEnvironment(required = "client_assertion_claims")
	public Environment evaluate(Environment env) {

		String issuer = env.getString("client", "client_id");

		// TODO:  We should be using the "revocation_endpoint" as audience, but for compatiability reasons,
		//	we'll be using just the issuer
		String audience = env.getString("server", "issuer");

		if (Strings.isNullOrEmpty(issuer) || Strings.isNullOrEmpty(audience)) {
			throw error("Couldn't find required configuration element", args("issuer", issuer, "audience", audience));
		}

		JsonObject claims = new JsonObject();
		claims.addProperty("iss", issuer);
		claims.addProperty("sub", issuer);
		claims.addProperty("aud", audience);
		claims.addProperty("jti", RandomStringUtils.randomAlphanumeric(20));

		Instant iat = Instant.now();
		Instant exp = iat.plusSeconds(60);

		claims.addProperty("iat", iat.getEpochSecond());
		claims.addProperty("exp", exp.getEpochSecond());

		logSuccess("Created client assertion claims", claims);

		env.putObject("client_assertion_claims", claims);

		return env;

	}

}
