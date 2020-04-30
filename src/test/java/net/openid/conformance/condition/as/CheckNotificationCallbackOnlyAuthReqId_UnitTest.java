package net.openid.conformance.condition.as;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CheckNotificationCallbackOnlyAuthReqId_UnitTest {
	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private CheckNotificationCallbackOnlyAuthReqId cond;

	@Before
	public void setUp() throws Exception {
		cond = new CheckNotificationCallbackOnlyAuthReqId();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseNull() {
		env.putObject("notification_callback", new JsonObject());

		cond.execute(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseEmpty() {
		JsonObject o = new JsonObject();
		o.add("body_json", new JsonObject());
		env.putObject("notification_callback", o);

		cond.execute(env);
	}

	@Test
	public void testEvaluate_caseGood() {
		JsonObject o = new JsonObject();
		o.add("body_json", new JsonParser().parse("{\"auth_req_id\": \"1c266114-a1be-4252-8ad1-04986c5b9ac1\"}").getAsJsonObject());
		env.putObject("notification_callback", o);

		cond.execute(env);
	}

	@Test(expected = ConditionError.class)
	public void testEvaluate_caseMultipleValues() {
		JsonObject o = new JsonObject();
		o.add("body_json", new JsonParser().parse("{\"auth_req_id\": \"1c266114-a1be-4252-8ad1-04986c5b9ac1\",\"expires_in\": 3600}").getAsJsonObject());
		env.putObject("notification_callback", o);

		cond.execute(env);
	}
}
