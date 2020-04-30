package net.openid.conformance.condition.client;

import java.util.Arrays;

import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class FAPIOBCheckDiscEndpointClaimsSupported extends AbstractValidateJsonArray {

	private static final String environmentVariable = "claims_supported";

	private static final String[] SET_VALUES = new String[] { "acr", "openbanking_intent_id" };
	private static final int minimumMatchesRequired = SET_VALUES.length;

	private static final String errorMessageNotEnough = "The server does not support enough of the required claims.";

	@Override
	@PreEnvironment(required = "server")
	public Environment evaluate(Environment env) {

		return validate(env, environmentVariable, Arrays.asList(SET_VALUES), minimumMatchesRequired,
				errorMessageNotEnough);
	}


}
