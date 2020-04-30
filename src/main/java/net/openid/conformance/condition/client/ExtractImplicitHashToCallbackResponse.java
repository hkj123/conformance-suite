package net.openid.conformance.condition.client;

import java.nio.charset.Charset;
import java.util.List;

import net.openid.conformance.testmodule.Environment;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;

public class ExtractImplicitHashToCallbackResponse extends AbstractCondition {

	@Override
	@PreEnvironment(strings = "implicit_hash")
	@PostEnvironment(required = "callback_params")
	public Environment evaluate(Environment env) {
		String implicit_hash = env.getString("implicit_hash");
		if (!Strings.isNullOrEmpty(implicit_hash)) {

			String hash = implicit_hash.substring(1); // strip off the leading # character

			List<NameValuePair> parameters = URLEncodedUtils.parse(hash, Charset.defaultCharset());

			log("Extracted response from URL fragment", args("parameters", parameters));

			JsonObject o = new JsonObject();
			for (NameValuePair pair : parameters) {
				o.addProperty(pair.getName(), pair.getValue());
			}

			env.putObject("callback_params", o);

			logSuccess("Extracted the hash values", o);

			return env;

		}

		JsonObject o = new JsonObject();
		env.putObject("callback_params", o);
		logSuccess("implicit_hash is empty", o);

		return env;
	}

}
