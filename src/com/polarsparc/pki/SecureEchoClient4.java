/*
 *
 *  Name:        SecureEchoClient4
 *  Description: Echo client that loads both the KeyStore and the TrustStore and uses SSLContext
 *  
 */

package com.polarsparc.pki;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SecureEchoClient4 {
	private static final int _SSL_PORT = 8443;
	private static final String _PROTOCOL = "TLSv1.2";
	private static final String _SSL_HOST = "localhost";
	
	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.printf("Usage: java com.polarsparc.pki.SecureEchoClient4 <keystore> <ks-secret> <truststore> <ts-secret> <message>\n");
			System.exit(1);
		}
		
		try {
			String kst = KeyStore.getDefaultType();
			
			System.out.printf("Echo (client-4), default key store type: %s\n", kst);
			
			KeyStore ks = KeyStore.getInstance(kst);
			try (InputStream fs = new FileInputStream(args[0])) {
				ks.load(fs, args[1].toCharArray());
			}
			catch (Exception ioEx) {
				throw ioEx;
			}
			
			String ksa = KeyManagerFactory.getDefaultAlgorithm();
			
			System.out.printf("Echo (client-4), default key manager algorithm: %s\n", ksa);
			
			KeyManagerFactory ksf = KeyManagerFactory.getInstance(ksa);
			ksf.init(ks, args[1].toCharArray());
			
			KeyStore ts = KeyStore.getInstance(kst);
			try (InputStream fs = new FileInputStream(args[2])) {
				ts.load(fs, args[3].toCharArray());
			}
			catch (Exception ioEx) {
				throw ioEx;
			}
			
			String tsa = TrustManagerFactory.getDefaultAlgorithm();
			
			System.out.printf("Echo (client-4), default trust manager algorithm: %s\n", tsa);
			
			TrustManagerFactory tsf = TrustManagerFactory.getInstance(tsa);
			tsf.init(ts);
			
			KeyManager[] km = ksf.getKeyManagers();
			
			TrustManager[] tm = tsf.getTrustManagers();
			
			SSLContext context = SSLContext.getInstance(_PROTOCOL);
			context.init(km, tm, null);
			
			SSLSocketFactory factory = context.getSocketFactory();
			
			SSLSocket socket = (SSLSocket) factory.createSocket(_SSL_HOST, _SSL_PORT);
			
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			output.write(args[4]+"\n");
			output.flush();
			
			socket.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
