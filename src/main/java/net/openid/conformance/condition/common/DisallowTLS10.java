package net.openid.conformance.condition.common;

import com.google.common.base.Strings;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.util.FAPITLSClient;
import org.bouncycastle.crypto.tls.AlertDescription;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.TlsClient;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsFatalAlert;
import org.bouncycastle.crypto.tls.TlsFatalAlertReceived;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.SecureRandom;

public class DisallowTLS10 extends AbstractCondition {

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

				TlsClient client = new FAPITLSClient(tlsTestHost, false, ProtocolVersion.TLSv10);

				protocol.connect(client);

				// By the time handshake completes an exception should have been thrown, but just in case:
				throw error("Connection completed unexpectedly");

			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					// Don't care
				}
			}
		} catch (FAPITLSClient.ServerHelloReceived e) {
			ProtocolVersion serverVersion = e.getServerVersion();
			if (serverVersion == ProtocolVersion.TLSv10) {
				throw error("The server accepted a TLS 1.0 connection. This is not permitted by the specification.", args("host", tlsTestHost, "port", tlsTestPort));
			} else {
				throw error("Server used different TLS version than requested",
					args("server_version", serverVersion.toString(),
						"host", tlsTestHost,
						"port", tlsTestPort));
			}
		} catch (IOException e) {
			if ((e instanceof TlsFatalAlertReceived)
				&& (((TlsFatalAlertReceived) e).getAlertDescription() == AlertDescription.handshake_failure ||
					((TlsFatalAlertReceived) e).getAlertDescription() == AlertDescription.protocol_version)){
				// If we get here then we haven't received a server hello agreeing on a version
				logSuccess("Server refused TLS 1.0 handshake", args("host", tlsTestHost, "port", tlsTestPort));
				return env;
			} else if ((e instanceof TlsFatalAlert)
				&& ((TlsFatalAlert) e).getAlertDescription() == AlertDescription.handshake_failure) {
				// If we get here then we haven't received a server hello agreeing on a version
				logSuccess("Server refused TLS 1.0 handshake", args("host", tlsTestHost, "port", tlsTestPort));
				return env;
			} else if ((e instanceof SocketException)
				&& ((SocketException) e).getMessage().equals("Connection reset")) {
				// AWS ELB seem to reject like this instead of by failing the handshake
				logSuccess("Server refused TLS 1.0 handshake", args("host", tlsTestHost, "port", tlsTestPort));
				return env;
			} else {
				throw error("Failed to make TLS connection, but in a different way than expected", e, args("host", tlsTestHost, "port", tlsTestPort));
			}
		}

	}

}
