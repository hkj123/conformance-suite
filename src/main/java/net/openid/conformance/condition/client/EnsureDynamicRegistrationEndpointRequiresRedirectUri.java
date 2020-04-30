package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;

public class EnsureDynamicRegistrationEndpointRequiresRedirectUri extends AbstractCondition {

	private static final Logger logger = LoggerFactory.getLogger(EnsureDynamicRegistrationEndpointRequiresRedirectUri.class);

	@Override
	@PreEnvironment(required = {"server", "dynamic_registration_request"})
	public Environment evaluate(Environment env) {

		if (env.getString("server", "registration_endpoint") == null) {
			throw error("Couldn't find registration endpoint");
		}

		if (!env.containsObject("dynamic_registration_request")){
			throw error("Coudln't find dynamic registration request");
		}

		JsonObject requestObj = env.getObject("dynamic_registration_request");

		try {

			RestTemplate restTemplate = createRestTemplate(env);
			HttpHeaders headers = new HttpHeaders();

			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<?> request = new HttpEntity<>(requestObj.toString(), headers);

			String jsonString = null;

			try {
				jsonString = restTemplate.postForObject(env.getString("server", "registration_endpoint"), request, String.class);
				throw error("Registration endpoint returned successful response for a request with no redirect URI", args("body", jsonString));
			} catch (RestClientResponseException e) {
				if (e.getRawStatusCode() == HttpStatus.SC_BAD_REQUEST) {
					logSuccess("Registration endpoint refused request", args("code", e.getRawStatusCode(), "status", e.getStatusText()));
					return env;
				} else {
					throw error("Error from the registration endpoint", e, args("code", e.getRawStatusCode(), "status", e.getStatusText()));
				}
			}


		} catch (NoSuchAlgorithmException | KeyManagementException | CertificateException | InvalidKeySpecException | KeyStoreException | IOException | UnrecoverableKeyException e) {
			logger.warn("Error creating HTTP Client", e);
			throw error("Error creating HTTP Client", e);
		}
	}
}
