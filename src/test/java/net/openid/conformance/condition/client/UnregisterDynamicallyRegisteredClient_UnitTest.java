package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.badRequest;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.noContent;

@RunWith(MockitoJUnitRunner.class)
public class UnregisterDynamicallyRegisteredClient_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	@ClassRule
	public static HoverflyRule hoverfly = HoverflyRule.inSimulationMode(dsl(
		service("good.example.com")
			.delete("/deregister")
			.anyBody()
			.willReturn(noContent()),
		service("bad.example.com")
			.delete("/deregister")
			.anyBody()
			.willReturn(badRequest())));

	private UnregisterDynamicallyRegisteredClient cond;

	@Before
	public void setUp() throws Exception {
		hoverfly.resetJournal();
		cond = new UnregisterDynamicallyRegisteredClient();
		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);
	}

	/**
	 * Test for {@link UnregisterDynamicallyRegisteredClient#evaluate(Environment)}
	 */
	@Test
	public void testEvaluate_noErrors(){
		JsonObject client = new JsonObject();
		client.addProperty("registration_access_token", "reg.access.token");
		client.addProperty("registration_client_uri", "https://good.example.com/deregister");
		env.putObject("client", client);
		cond.execute(env);
		hoverfly.verify(service("good.example.com")
			.delete("/deregister")
			.header("Authorization", "Bearer reg.access.token"));
	}

	/**
	 * Test for {@link UnregisterDynamicallyRegisteredClient#evaluate(Environment)}
	 */
	@Test(expected = ConditionError.class)
	public void testEvaluate_badResponse(){
		JsonObject client = new JsonObject();
		client.addProperty("registration_access_token", "reg.access.token");
		client.addProperty("registration_client_uri", "https://bad.example.com/deregister");
		env.putObject("client", client);
		cond.execute(env);
	}


}
