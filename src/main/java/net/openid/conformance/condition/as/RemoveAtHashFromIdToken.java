package net.openid.conformance.condition.as;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class RemoveAtHashFromIdToken extends AbstractCondition {

	@Override
	@PreEnvironment(required = "id_token_claims")
	@PostEnvironment(required = "id_token_claims")
	public Environment evaluate(Environment env) {

		JsonObject claims = env.getObject("id_token_claims");

		claims.addProperty("at_hash", ((String) null));

		env.putObject("id_token_claims", claims);

		logSuccess("Removed at_hash value from ID token claims", args("id_token_claims", claims));

		return env;

	}

}
