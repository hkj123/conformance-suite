package net.openid.conformance.fapiciba;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.condition.client.CallAutomatedCibaApprovalEndpoint;
import net.openid.conformance.condition.client.CheckTokenEndpointHttpStatusNot200;
import net.openid.conformance.condition.client.ExpectAccessDeniedErrorFromTokenEndpointDueToUserRejectingRequest;
import net.openid.conformance.condition.client.TellUserToRejectCIBAAuthentication;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.testmodule.TestFailureException;
import net.openid.conformance.testmodule.TestModule;
import net.openid.conformance.variant.CIBAMode;

@PublishTestModule(
	testName = "fapi-ciba-id1-user-rejects-authentication",
	displayName = "FAPI-CIBA-ID1: user rejects authentication",
	summary = "This test requires the user to reject the authentication on their device, for example by pressing the 'cancel' button on the login screen. It verifies the error is correctly notified back to the relying party.",
	profile = "FAPI-CIBA-ID1",
	configurationFields = {
		"server.discoveryUrl",
		"client.scope",
		"client.jwks",
		"client.hint_type",
		"client.hint_value",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl"
	}
)
public class FAPICIBAID1UserRejectsAuthentication extends AbstractFAPICIBAID1 {

	@Override
	protected void callAutomatedEndpoint() {
		env.putString("request_action", "deny");
		callAndStopOnFailure(CallAutomatedCibaApprovalEndpoint.class);
	}

	@Override
	protected void waitForAuthenticationToComplete(long delaySeconds) {
		callAndStopOnFailure(TellUserToRejectCIBAAuthentication.class);

		if (testType == CIBAMode.PING) {
			// test resumes when notification endpoint called
			setStatus(Status.WAITING);
			return;
		}

		for (int attempts = 0; attempts < 20; attempts++) {
			setStatus(TestModule.Status.WAITING);
			try {
				Thread.sleep(delaySeconds * 1000);
			} catch (InterruptedException e) {
				throw new TestFailureException(getId(), "Thread.sleep threw exception: " + e.getMessage());
			}
			setStatus(TestModule.Status.RUNNING);

			eventLog.startBlock(currentClientString() + "Polling token endpoint waiting for user to reject authentication");
			callTokenEndpointForCibaGrant();
			eventLog.endBlock();

			callAndStopOnFailure(CheckTokenEndpointHttpStatusNot200.class);

			String error = env.getString("token_endpoint_response", "error");
			if (error.equals("access_denied")) {

				verifyTokenEndpointResponseIsAccessDeniedAndFinishTest();
				return;
			}

			verifyTokenEndpointResponseIsPendingOrSlowDown();

			if (delaySeconds < 60) {
				delaySeconds *= 1.5;
			}
		}

		throw new TestFailureException(getId(), "User did not reject authentication before timeout");
	}

	@Override
	protected void processNotificationCallback(JsonObject requestParts) {
		if (testType == CIBAMode.PING) {
			processPingNotificationCallback(requestParts);
			verifyTokenEndpointResponseIsAccessDeniedAndFinishTest();
		} else {
			super.processNotificationCallback(requestParts);
		}
	}

	protected void verifyTokenEndpointResponseIsAccessDeniedAndFinishTest() {
		eventLog.startBlock(currentClientString() + "Verify token endpoint response is access_denied");

		checkStatusCode400AndValidateErrorFromTokenEndpointResponse();

		callAndStopOnFailure(ExpectAccessDeniedErrorFromTokenEndpointDueToUserRejectingRequest.class);

		eventLog.endBlock();
		fireTestFinished();
	}


}
