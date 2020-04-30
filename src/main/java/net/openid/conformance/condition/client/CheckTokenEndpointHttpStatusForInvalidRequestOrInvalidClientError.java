package net.openid.conformance.condition.client;

import com.google.common.base.Strings;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import org.apache.http.HttpStatus;

public class CheckTokenEndpointHttpStatusForInvalidRequestOrInvalidClientError extends AbstractCondition {

	@Override
	@PreEnvironment(required = "token_endpoint_response")
	public Environment evaluate(Environment env) {
		Integer httpStatus = env.getInteger("token_endpoint_response_http_status");

		if (httpStatus == null) {
			throw error("Http status can not be null.");
		}

		String error = env.getString("token_endpoint_response", "error");

		if (Strings.isNullOrEmpty(error)) {
			throw error("Couldn't find error field");
		}

		if (error.equals("invalid_request")) {

			if (httpStatus != HttpStatus.SC_BAD_REQUEST) {

				throw error("Invalid http status for error invalid_request" , args("actual", httpStatus, "expected", HttpStatus.SC_BAD_REQUEST));
			}
		} else if (error.equals("invalid_client")) {

			if (httpStatus != HttpStatus.SC_BAD_REQUEST &&
				httpStatus != HttpStatus.SC_UNAUTHORIZED) {

				throw error("Invalid http status for error invalid_client", args("actual", httpStatus, "expected", "400 or 401"));
			}
		} else {

			throw error("error is neither invalid_request nor invalid_client", args("httpstatus", httpStatus, "error", error));
		}

		logSuccess("Token endpoint http status code was " + httpStatus + " for error '"+error+"'");

		return env;
	}
}
