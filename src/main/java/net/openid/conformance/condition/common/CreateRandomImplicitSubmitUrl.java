package net.openid.conformance.condition.common;

import net.openid.conformance.testmodule.Environment;
import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;

/**
 * Creates a callback URL based on the base_url environment value
 */
public class CreateRandomImplicitSubmitUrl extends AbstractCondition {

	@Override
	@PreEnvironment(strings = "base_url")
	@PostEnvironment(required = "implicit_submit")
	public Environment evaluate(Environment env) {
		String baseUrl = env.getString("base_url");

		if (baseUrl.isEmpty()) {
			throw error("Base URL is empty");
		}

		// create a random submission URL
		String path = "implicit/" + RandomStringUtils.randomAlphanumeric(20);

		JsonObject o = new JsonObject();
		o.addProperty("path", path);
		o.addProperty("fullUrl", baseUrl + "/" + path);

		env.putObject("implicit_submit", o);

		logSuccess("Created random implicit submission URL",
			args("implicit_submit", o));

		return env;
	}

}
