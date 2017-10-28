/*
 *
 *  Name:        SecureEchoClient3
 *  Description: Echo client that loads a TrustStore and uses SSLContext
 *  
 */

package com.polarsparc.pki;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SecureEchoClient3 {
	private static final int _SSL_PORT = 8443;
	private static final String _PROTOCOL = "TLSv1.2";
	private static final String _SSL_HOST = "localhost";
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.printf("Usage: java com.polarsparc.pki.SecureEchoClient3 <truststore> <password> <message>\n");
			System.exit(1);
		}
		
		try {
			String kst = KeyStore.getDefaultType();
			
			System.out.printf("Echo (client-3), default key store type: %s\n", kst);
			
			KeyStore ks = KeyStore.getInstance(kst);
			try (InputStream fs = new FileInputStream(args[0])) {
				ks.load(fs, args[1].toCharArray());
			}
			catch (Exception ioEx) {
				throw ioEx;
			}
			
			String tsa = TrustManagerFactory.getDefaultAlgorithm();
			
			System.out.printf("Echo (client-3), default trust manager algorithm: %s\n", tsa);
			
			TrustManagerFactory tsf = TrustManagerFactory.getInstance(tsa);
			tsf.init(ks);
			
			TrustManager[] tm = tsf.getTrustManagers();
			
			SSLContext context = SSLContext.getInstance(_PROTOCOL);
			context.init(null, tm, null);
			
			SSLSocketFactory factory = context.getSocketFactory();
			
			SSLSocket socket = (SSLSocket) factory.createSocket(_SSL_HOST, _SSL_PORT);
			
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			output.write(args[2]+"\n");
			output.flush();
			
			socket.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
