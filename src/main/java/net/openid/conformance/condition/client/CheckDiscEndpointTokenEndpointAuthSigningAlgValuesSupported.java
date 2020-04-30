package net.openid.conformance.condition.client;

import com.google.gson.JsonElement;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

import java.util.Arrays;

public class CheckDiscEndpointTokenEndpointAuthSigningAlgValuesSupported extends AbstractValidateJsonArray {

	private static final String environmentVariable = "token_endpoint_auth_signing_alg_values_supported";

	private static final String[] SET_VALUES = new String[] { "PS256", "ES256" };

	private static final String errorMessageNotEnough = "No matching value from server";

	@Override
	@PreEnvironment(required = "server")
	public Environment evaluate(Environment env) {

		final String[] valuesRequired = new String[] { "private_key_jwt" };
		JsonElement serverValues = env.getElementFromObject("server", "token_endpoint_auth_methods_supported");

		if ( serverValues != null && serverValues.isJsonArray() ) {

			if ( countMatchingElements(Arrays.asList(valuesRequired), serverValues.getAsJsonArray()) > 0 ) {
				return validate(env, environmentVariable, Arrays.asList(SET_VALUES), 1,
						errorMessageNotEnough);
			}
		}

		String logMessage = "Not checking token_endpoint_auth_signing_alg_values_supported as token_endpoint_auth_methods_supported does not contain the method (private_key_jwt ) that requires signing";

		logSuccess(logMessage, args("actual", serverValues, "expected", valuesRequired));
		return env;
	}
}
