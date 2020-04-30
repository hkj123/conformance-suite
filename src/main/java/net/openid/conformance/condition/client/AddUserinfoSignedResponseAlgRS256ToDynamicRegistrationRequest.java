package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class AddUserinfoSignedResponseAlgRS256ToDynamicRegistrationRequest extends AbstractCondition {

	@Override
	@PreEnvironment(required = "dynamic_registration_request", strings = "base_url")
	@PostEnvironment(required = "dynamic_registration_request")
	public Environment evaluate(Environment env) {

		JsonObject dynamicRegistrationRequest = env.getObject("dynamic_registration_request");

		dynamicRegistrationRequest.addProperty("userinfo_signed_response_alg", "RS256");

		env.putObject("dynamic_registration_request", dynamicRegistrationRequest);

		log("Added userinfo_signed_response_alg=RS256 to dynamic registration request", args("dynamic_registration_request", dynamicRegistrationRequest));

		return env;
	}

}
