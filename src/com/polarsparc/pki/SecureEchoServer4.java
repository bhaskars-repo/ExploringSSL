/*
 *
 *  Name:        SecureEchoServer4
 *  Description: Echo server that loads both the KeyStore and the TrustStore and uses SSLContext
 *  
 */

package com.polarsparc.pki;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SecureEchoServer4 {
	private static final int _SSL_PORT = 8443;
	private static final String _PROTOCOL = "TLSv1.2";
	
	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.printf("Usage: java com.polarsparc.pki.SecureEchoServer4 <keystore> <ks-secret> <truststore> <ts-secret>\n");
			System.exit(1);
		}
		
		try {
			String kst = KeyStore.getDefaultType();
			
			System.out.printf("Echo (server-4), default key store type: %s\n", kst);
			
			KeyStore ks = KeyStore.getInstance(kst);
			try (InputStream fs = new FileInputStream(args[0])) {
				ks.load(fs, args[1].toCharArray());
			}
			catch (Exception ioEx) {
				throw ioEx;
			}
			
			String ksa = KeyManagerFactory.getDefaultAlgorithm();
			
			System.out.printf("Echo (server-4), default key manager algorithm: %s\n", ksa);
			
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
			
			System.out.printf("Echo (server-4), default trust manager algorithm: %s\n", tsa);
			
			TrustManagerFactory tsf = TrustManagerFactory.getInstance(tsa);
			tsf.init(ts);
			
			KeyManager[] km = ksf.getKeyManagers();
			
			TrustManager[] tm = tsf.getTrustManagers();
			
			SSLContext context = SSLContext.getInstance(_PROTOCOL);
			context.init(km, tm, null);
			
			SSLServerSocketFactory factory = context.getServerSocketFactory();
			
			SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(_SSL_PORT);
			server.setNeedClientAuth(true);
			
			System.out.printf("Echo (server-4) started on %d\n", _SSL_PORT);
			
			for (;;) {
				try (SSLSocket client = (SSLSocket) server.accept()) {
					try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
						String line = null;
						while ((line = input.readLine()) != null) {
							System.out.printf("-> Echo (server-4): %s\n", line);
							System.out.flush();
						}
					}
					catch (Exception inputEx) {
						inputEx.printStackTrace();
					}
				}
				catch (Exception sockEx) {
					sockEx.printStackTrace();
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
