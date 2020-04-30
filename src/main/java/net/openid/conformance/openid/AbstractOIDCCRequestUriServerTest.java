package net.openid.conformance.openid;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.AddRequestUriToDynamicRegistrationRequest;
import net.openid.conformance.condition.client.CheckDiscEndpointRequestUriParameterSupported;
import net.openid.conformance.condition.common.CreateRandomRequestUri;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Generic behaviour required when testing request_uri behaviours
 */
public abstract class AbstractOIDCCRequestUriServerTest extends AbstractOIDCCServerTest {
    @Override
    protected void createDynamicClientRegistrationRequest() {
        super.createDynamicClientRegistrationRequest();

        callAndStopOnFailure(CreateRandomRequestUri.class, "OIDCC-6.2");
        callAndStopOnFailure(AddRequestUriToDynamicRegistrationRequest.class);
    }

    @Override
    protected void onConfigure(JsonObject config, String baseUrl) {

        super.onConfigure(config, baseUrl);
        callAndContinueOnFailure(CheckDiscEndpointRequestUriParameterSupported.class, Condition.ConditionResult.FAILURE, "OIDCD-3");

    }

    @Override
    public Object handleHttp(String path, HttpServletRequest req, HttpServletResponse res, HttpSession session, JsonObject requestParts) {

        if (path.equals(env.getString("request_uri", "path"))) {
            return handleRequestUriRequest(requestParts);
        }
        return super.handleHttp(path, req, res, session, requestParts);

    }

    private Object handleRequestUriRequest(JsonObject requestParts) {
        String requestObject = env.getString("request_object");

        return ResponseEntity.ok()
            .contentType(DATAUTILS_MEDIATYPE_APPLICATION_JOSE)
            .body(requestObject);
    }

    @Override
	abstract protected void createAuthorizationRedirect();

}
