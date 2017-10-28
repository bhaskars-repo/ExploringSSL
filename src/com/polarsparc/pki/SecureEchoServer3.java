/*
 *
 *  Name:        SecureEchoServer3
 *  Description: Echo server that loads a KeyStore and uses SSLContext
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

public class SecureEchoServer3 {
	private static final int _SSL_PORT = 8443;
	private static final String _PROTOCOL = "TLSv1.2";
	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.printf("Usage: java com.polarsparc.pki.SecureEchoServer3 <keystore> <password>\n");
			System.exit(1);
		}
		
		try {
			String kst = KeyStore.getDefaultType();
			
			System.out.printf("Echo (server-3), default key store type: %s\n", kst);
			
			KeyStore ks = KeyStore.getInstance(kst);
			try (InputStream fs = new FileInputStream(args[0])) {
				ks.load(fs, args[1].toCharArray());
			}
			catch (Exception ioEx) {
				throw ioEx;
			}
			
			String ksa = KeyManagerFactory.getDefaultAlgorithm();
			
			System.out.printf("Echo (server-3), default key manager algorithm: %s\n", ksa);
			
			KeyManagerFactory ksf = KeyManagerFactory.getInstance(ksa);
			ksf.init(ks, args[1].toCharArray());
			
			KeyManager[] km = ksf.getKeyManagers();
			
			SSLContext context = SSLContext.getInstance(_PROTOCOL);
			context.init(km, null, null);
			
			SSLServerSocketFactory factory = context.getServerSocketFactory();
			
			SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(_SSL_PORT);
			
			System.out.printf("Echo (server-3) started on %d\n", _SSL_PORT);
			
			for (;;) {
				try (SSLSocket client = (SSLSocket) server.accept()) {
					try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
						String line = null;
						while ((line = input.readLine()) != null) {
							System.out.printf("-> Echo (server-3): %s\n", line);
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
