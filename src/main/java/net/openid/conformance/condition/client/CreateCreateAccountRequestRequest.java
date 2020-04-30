package net.openid.conformance.condition.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class CreateCreateAccountRequestRequest extends AbstractCondition {

	@Override
	@PreEnvironment()
	@PostEnvironment(required = "account_requests_endpoint_request")
	public Environment evaluate(Environment env) {

		JsonArray permissions = new JsonArray();
		permissions.add("ReadAccountsBasic");

		JsonObject data = new JsonObject();
		data.add("Permissions", permissions);

		JsonObject o = new JsonObject();
		o.add("Data", data);
		o.add("Risk", new JsonObject());

		env.putObject("account_requests_endpoint_request", o);

		logSuccess(args("account_requests_endpoint_request", o));

		return env;
	}

}
