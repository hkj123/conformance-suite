package net.openid.conformance.condition.client;

import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

import java.util.Arrays;

public class OIDCCCheckDiscEndpointScopesSupported extends AbstractValidateJsonArray {

	private static final String environmentVariable = "scopes_supported";

	private static final String EXPECTED_VALUE = "openid";
	private static final int minimumMatchesRequired = 1;

	private static final String errorMessageNotEnough = "The server does not support the required scopes";

	@Override
	@PreEnvironment(required = "server")
	public Environment evaluate(Environment env) {

		return validate(env, environmentVariable, Arrays.asList(EXPECTED_VALUE), minimumMatchesRequired, errorMessageNotEnough);

	}

}
