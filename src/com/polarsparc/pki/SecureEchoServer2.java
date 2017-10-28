/*
 *
 *  Name:        SecureEchoServer2
 *  Description: Echo server that uses the secure sockets with insecure ciphers
 *  
 */

package com.polarsparc.pki;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SecureEchoServer2 {
	private static final int _SSL_PORT = 8443;
	
	public static void main(String[] args) {
		try {
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			
			SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(_SSL_PORT);
			server.setEnabledCipherSuites(server.getSupportedCipherSuites()); // Insecure
			
			System.out.printf("Echo (server2) started on %d\n", _SSL_PORT);
			
			for (;;) {
				try (SSLSocket client = (SSLSocket) server.accept()) {
					try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
						String line = null;
						while ((line = input.readLine()) != null) {
							System.out.printf("-> Echo (server2): %s\n", line);
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
