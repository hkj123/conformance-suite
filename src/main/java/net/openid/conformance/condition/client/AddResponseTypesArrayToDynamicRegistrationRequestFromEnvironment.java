package net.openid.conformance.condition.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class AddResponseTypesArrayToDynamicRegistrationRequestFromEnvironment extends AbstractCondition {

	@Override
	@PreEnvironment(required = "dynamic_registration_request", strings = "response_type")
	@PostEnvironment(required = "dynamic_registration_request")
	public Environment evaluate(Environment env) {

		JsonObject dynamicRegistrationRequest = env.getObject("dynamic_registration_request");
		String responseType = env.getString("response_type");

		JsonArray responseTypes = new JsonArray();
		responseTypes.add(responseType);

		dynamicRegistrationRequest.add("response_types", responseTypes);

		env.putObject("dynamic_registration_request", dynamicRegistrationRequest);

		log("Added response_types array to dynamic registration request", args("dynamic_registration_request", dynamicRegistrationRequest));

		return env;
	}
}
