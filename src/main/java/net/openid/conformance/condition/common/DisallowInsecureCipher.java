package net.openid.conformance.condition.common;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import org.bouncycastle.crypto.tls.AlertDescription;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.NameType;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.ServerName;
import org.bouncycastle.crypto.tls.ServerNameList;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClient;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsCredentials;
import org.bouncycastle.crypto.tls.TlsExtensionsUtils;
import org.bouncycastle.crypto.tls.TlsFatalAlert;
import org.bouncycastle.crypto.tls.TlsFatalAlertReceived;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class DisallowInsecureCipher extends AbstractCondition {

	private static final List<Integer> ALLOWED_CIPHERS = ImmutableList.of(
		CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
		CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
		CipherSuite.TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,
		CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384);

	private static final Map<Integer, String> CIPHER_NAMES = new HashMap<>();

	static {
		// Reflect on BouncyCastle to get a list of supported ciphers and names
		for (Field field : CipherSuite.class.getDeclaredFields()) {
			String name = field.getName();
			int modifiers = field.getModifiers();
			Class<?> type = field.getType();
			final int PUBLIC_STATIC_FINAL = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

			if (type.isPrimitive()
				&& type.getName().equals("int")
				&& ((modifiers & PUBLIC_STATIC_FINAL) == PUBLIC_STATIC_FINAL)) {
				try {
					int cipherId = field.getInt(null);
					if (!CipherSuite.isSCSV(cipherId)) {
						CIPHER_NAMES.put(cipherId, name);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// This is not expected to happen; but we'll log it just in case
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@PreEnvironment(required = "tls")
	public Environment evaluate(Environment env) {

		String tlsTestHost = env.getString("tls", "testHost");
		Integer tlsTestPort = env.getInteger("tls", "testPort");

		if (Strings.isNullOrEmpty(tlsTestHost)) {
			throw error("Couldn't find host to connect for TLS");
		}

		if (tlsTestPort == null) {
			throw error("Couldn't find port to connect for TLS");
		}

		try {
			Socket socket = setupSocket(tlsTestHost, tlsTestPort);

			try {

				TlsClientProtocol protocol = new TlsClientProtocol(socket.getInputStream(), socket.getOutputStream(), new SecureRandom());

				TlsClient client = new DefaultTlsClient() {

					@Override
					public TlsAuthentication getAuthentication() {
						return new TlsAuthentication() {

							@Override
							public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException {
								return null;
							}

							@Override
							public void notifyServerCertificate(Certificate serverCertificate) throws IOException {
								// Don't care
							}
						};
					}

					@Override
					public int[] getCipherSuites() {

						// filter the list of supported ciphers to contain only disallowed ciphers
						ArrayList<Integer> ciphers = Lists.newArrayList(CIPHER_NAMES.keySet());
						ciphers.removeAll(ALLOWED_CIPHERS);
						return Ints.toArray(ciphers);
					}

					@Override
					public ProtocolVersion getMinimumVersion() {
						return ProtocolVersion.TLSv12;
					}

					@Override
					@SuppressWarnings("rawtypes") // fit with the API
					public Hashtable getClientExtensions() throws IOException {
						Hashtable clientExtensions = super.getClientExtensions();
						Vector<ServerName> serverNameList = new Vector<>();
						serverNameList.addElement(new ServerName(NameType.host_name, tlsTestHost));
						TlsExtensionsUtils.addServerNameExtension(clientExtensions, new ServerNameList(serverNameList));
						return clientExtensions;
					}

					@Override
					public void notifySelectedCipherSuite(int selectedCipherSuite) {
						throw error("Server accepted a cipher that is not on the list of FAPI-RW permitted ciphers",
							args("host", tlsTestHost,
								"port", tlsTestPort,
								"cipher_suite", CIPHER_NAMES.get(selectedCipherSuite)));
					}
				};

				log("Trying to connect with a non-permitted cipher (this is not exhaustive: check the server configuration manually to verify conformance)",
					args("host", tlsTestHost, "port", tlsTestPort));

				protocol.connect(client);

				// By the time handshake completes an error should have been thrown, but just in case:
				throw error("Connection completed unexpectedly");

			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// Don't care
				}
			}
		} catch (IOException e) {
			if (e.getCause() instanceof ConditionError) {
				// It's our own error; pass it on
				throw (ConditionError) e.getCause();
			} else if ((e instanceof TlsFatalAlertReceived)
				&& ((TlsFatalAlertReceived) e).getAlertDescription() == AlertDescription.handshake_failure) {
				logSuccess("The TLS handshake was rejected when trying to connect with disallowed ciphers.", args("host", tlsTestHost, "port", tlsTestPort));
				return env;
			} else if ((e instanceof TlsFatalAlert)
				&& ((TlsFatalAlert) e).getAlertDescription() == AlertDescription.handshake_failure) {
				logSuccess("The TLS handshake failed when trying to connect with disallowed ciphers.", args("host", tlsTestHost, "port", tlsTestPort));
				return env;
			} else if ((e instanceof SocketException)
				&& ((SocketException) e).getMessage().equals("Connection reset")) {
				logSuccess("The TCP connection was reset when trying to connect with disallowed ciphers.", args("host", tlsTestHost, "port", tlsTestPort));
				return env;
			} else {
				throw error("Failed to make TLS connection, but in a different way than expected", e, args("host", tlsTestHost, "port", tlsTestPort));
			}
		}
	}

}
