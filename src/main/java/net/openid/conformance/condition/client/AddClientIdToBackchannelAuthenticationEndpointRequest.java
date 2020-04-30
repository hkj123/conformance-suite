package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class AddClientIdToBackchannelAuthenticationEndpointRequest extends AbstractCondition {

	@Override
	@PreEnvironment(required = { "backchannel_authentication_endpoint_request_form_parameters", "client" })
	@PostEnvironment(required = "backchannel_authentication_endpoint_request_form_parameters")
	public Environment evaluate(Environment env) {

		JsonObject o = env.getObject("backchannel_authentication_endpoint_request_form_parameters");

		o.addProperty("client_id", env.getString("client", "client_id"));

		env.putObject("backchannel_authentication_endpoint_request_form_parameters", o);

		log(o);

		return env;

	}

}
