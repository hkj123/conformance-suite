package net.openid.conformance.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;

import java.text.ParseException;
import java.util.List;

public class JWKUtil {
	public static JWKSet parseJWKSet(String jwksString) throws ParseException {
		JWKSet jwkSet = JWKSet.parse(jwksString);
		return jwkSet;
	}

	public static JsonObject getPublicJwksAsJsonObject(JWKSet jwks) {
		JsonObject publicJwks = new JsonParser().parse(jwks.toJSONObject(true).toJSONString()).getAsJsonObject();
		return publicJwks;
	}

	public static JsonObject getPrivateJwksAsJsonObject(JWKSet jwks) {
		JsonObject privateJwks = new JsonParser().parse(jwks.toJSONObject(false).toJSONString()).getAsJsonObject();
		return privateJwks;
	}

	/**
	 * Will select the first key with the correct type, use and alg if possible
	 * or will select the key with correct type and use
	 * or will select the key with correct type
	 * Note: Server jwks will probably contain only 1 matching key (we create it), but just in case...
	 * @param jwsAlgorithm
	 * @param keys
	 * @return null if not found
	 */
	public static JWK selectAsymmetricJWSKey(JWSAlgorithm jwsAlgorithm, List<JWK> keys) {
		JWK bestMatch = null;
		JWK secondBestMatch = null;
		JWK thirdMatch = null;
		//TODO consider using nimbusds JWKMatcher?
		for(JWK key : keys) {
			if(JWSAlgorithm.Family.EC.contains(jwsAlgorithm) && KeyType.EC.equals(key.getKeyType())) {
				ECKey ecKey = (ECKey)key;
				if(JWSAlgorithm.ES256.equals(jwsAlgorithm) && !Curve.P_256.equals(ecKey.getCurve())) {
					continue;
				}
				if(JWSAlgorithm.ES256K.equals(jwsAlgorithm) && !Curve.SECP256K1.equals(ecKey.getCurve())) {
					continue;
				}
				if(key.getKeyUse()!=null) {
					if(KeyUse.SIGNATURE.equals(key.getKeyUse())) {
						if(key.getAlgorithm()==null) {
							secondBestMatch = key;
						} else {
							if(key.getAlgorithm().equals(jwsAlgorithm)) {
								//this is the best match
								bestMatch = key;
								break;
							}
						}
					}
				} else {
					thirdMatch = key;
				}
			} else if(JWSAlgorithm.Family.ED.contains(jwsAlgorithm) && KeyType.OKP.equals(key.getKeyType())) {
				if(key.getKeyUse()!=null) {
					if(KeyUse.SIGNATURE.equals(key.getKeyUse())) {
						if(key.getAlgorithm()==null) {
							secondBestMatch = key;
						} else {
							if(key.getAlgorithm().equals(jwsAlgorithm)) {
								//this is the best match
								bestMatch = key;
								break;
							}
						}
					}
				} else {
					thirdMatch = key;
				}
			} else if(jwsAlgorithm.getName().startsWith("PS") && KeyType.RSA.equals(key.getKeyType())) {
				if(key.getKeyUse()!=null) {
					if(KeyUse.SIGNATURE.equals(key.getKeyUse())) {
						if(key.getAlgorithm()==null) {
							secondBestMatch = key;
						} else {
							if(key.getAlgorithm().equals(jwsAlgorithm)) {
								//this is the best match
								bestMatch = key;
								break;
							}
						}
					}
				} else {
					thirdMatch = key;
				}
			} else if(jwsAlgorithm.getName().startsWith("RS") && KeyType.RSA.equals(key.getKeyType())) {
				if(key.getKeyUse()!=null) {
					if(KeyUse.SIGNATURE.equals(key.getKeyUse())) {
						if(key.getAlgorithm()==null) {
							secondBestMatch = key;
						} else {
							if(key.getAlgorithm().equals(jwsAlgorithm)) {
								//this is the best match
								bestMatch = key;
								break;
							}
						}
					}
				} else {
					thirdMatch = key;
				}
			}
		}
		if(bestMatch!=null) {
			return bestMatch;
		} else if(secondBestMatch!=null) {
			return secondBestMatch;
		} else {
			return thirdMatch;
		}
	}
}
