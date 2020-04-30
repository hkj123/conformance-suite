package net.openid.conformance.condition.client;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

import java.text.ParseException;

public class InvalidateRequestObjectSignature extends AbstractCondition {

	@Override
	@PreEnvironment(strings = "request_object")
	@PostEnvironment(strings = "request_object")
	public Environment evaluate(Environment env) {

		String requestObjectString = env.getString("request_object");

		try {
			SignedJWT requestObject = SignedJWT.parse(requestObjectString);

			Base64URL signature = requestObject.getSignature();

			byte[] bytes = signature.decode();

			//Flip some of the bits in the signature to make it invalid
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] ^= 0x5A;
			}

			Base64URL invalidSignature = Base64URL.encode(bytes);

			//Rebuild the JWT using Base64URL
			Base64URL[] idTokenParsedParts = requestObject.getParsedParts();
			SignedJWT invalidRequestObject = new SignedJWT(
				idTokenParsedParts[0],
				idTokenParsedParts[1],
				invalidSignature);

			env.putString("request_object", invalidRequestObject.serialize());

			logSuccess("Made the request_object signature invalid", args("request_object", invalidRequestObject.serialize()));

			return env;

		} catch (ParseException e) {

			throw error("Couldn't parse JWT", e, args("request_object", requestObjectString));
		}
	}
}
