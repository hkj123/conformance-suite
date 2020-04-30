package net.openid.conformance.openid;

import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.EnsureIdTokenDoesNotContainName;
import net.openid.conformance.condition.client.EnsureMinimumAuthorizationCodeEntropy;
import net.openid.conformance.condition.client.EnsureMinimumAuthorizationCodeLength;
import net.openid.conformance.condition.client.ExtractAtHash;
import net.openid.conformance.condition.client.ExtractCHash;
import net.openid.conformance.condition.client.ValidateAtHash;
import net.openid.conformance.condition.client.ValidateCHash;
import net.openid.conformance.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "oidcc-server",
	displayName = "OIDCC",
	summary = "Tests primarily 'happy' flows",
	profile = "OIDCC",
	configurationFields = {
		"server.discoveryUrl"
	}
)
public class OIDCCServerTest extends AbstractOIDCCServerTest {

	@Override
	protected void performAuthorizationEndpointIdTokenValidation() {
		super.performAuthorizationEndpointIdTokenValidation();

		// OP-IDToken-at_hash
		callAndContinueOnFailure(ExtractAtHash.class,
				responseType.includesToken() ? Condition.ConditionResult.FAILURE : Condition.ConditionResult.INFO,
				"OIDCC-3.3.2.11");
		skipIfMissing(new String[] { "at_hash" }, null, Condition.ConditionResult.INFO,
				ValidateAtHash.class, Condition.ConditionResult.FAILURE, "OIDCC-3.3.2.11");

		// OP-IDToken_c_hash
		callAndContinueOnFailure(ExtractCHash.class,
				responseType.includesCode() ? Condition.ConditionResult.FAILURE : Condition.ConditionResult.INFO,
				"OIDCC-3.3.2.11");
		skipIfMissing(new String[] { "c_hash" }, null, Condition.ConditionResult.INFO ,
				ValidateCHash.class, Condition.ConditionResult.FAILURE, "OIDCC-3.3.2.11");
	}

	@Override
	protected void performTokenEndpointIdTokenValidation() {
		super.performTokenEndpointIdTokenValidation();

		// at_hash and c_hash are optional in the token endpoint id_token, but if present must be correct
		callAndContinueOnFailure(ExtractAtHash.class, Condition.ConditionResult.INFO, "OIDCC-3.3.2.11", "OIDCC-3.3.3.6");
		skipIfMissing(new String[] { "at_hash" }, null, Condition.ConditionResult.INFO,
			ValidateAtHash.class, Condition.ConditionResult.FAILURE, "OIDCC-3.3.2.11");

		callAndContinueOnFailure(ExtractCHash.class, Condition.ConditionResult.INFO, "OIDCC-3.3.2.11", "OIDCC-3.3.3.6");
		skipIfMissing(new String[] { "c_hash" }, null, Condition.ConditionResult.INFO ,
			ValidateCHash.class, Condition.ConditionResult.FAILURE, "OIDCC-3.3.2.11");
	}

	@Override
	protected void performIdTokenValidation() {
		super.performIdTokenValidation();

		// the python test did not check this as far as I know
		callAndContinueOnFailure(EnsureIdTokenDoesNotContainName.class, Condition.ConditionResult.WARNING,  "OIDCC-5.5", "OIDC-5.5.1");
	}

	@Override
	protected void performAuthorizationCodeValidation() {
		super.performAuthorizationCodeValidation();

		callAndContinueOnFailure(EnsureMinimumAuthorizationCodeLength.class, Condition.ConditionResult.FAILURE, "RFC6749-10.10", "RFC6819-5.1.4.2-2");

		callAndContinueOnFailure(EnsureMinimumAuthorizationCodeEntropy.class, Condition.ConditionResult.FAILURE, "RFC6749-10.10", "RFC6819-5.1.4.2-2");
	}
}
