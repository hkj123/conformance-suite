package net.openid.conformance.condition.as;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class OIDCCGenerateServerConfigurationIdTokenSigningAlgRS256Only extends OIDCCGenerateServerConfiguration {

	@Override
	protected void addIdTokenSigningAlgValuesSupported(JsonObject server) {
		JsonArray values = new JsonArray();
		values.add("RS256");
		server.add("id_token_signing_alg_values_supported", values);
	}
}
