package net.openid.conformance.condition.rs;

import net.openid.conformance.testmodule.Environment;
import org.apache.commons.lang3.RandomStringUtils;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;

public class GenerateAccountRequestId extends AbstractCondition {

	@Override
	@PostEnvironment(strings = "account_request_id")
	public Environment evaluate(Environment env) {

		String acct = RandomStringUtils.randomAlphanumeric(10);

		env.putString("account_request_id", acct);

		logSuccess("Created account request", args("account_request_id", acct));

		return env;

	}

}
