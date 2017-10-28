/*
 *
 *  Name:        SecureEchoServer
 *  Description: Echo server that uses the secure sockets
 *  
 */

package com.polarsparc.pki;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SecureEchoServer {
	private static final int _SSL_PORT = 8443;
	
	public static void main(String[] args) {
		try {
			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			
			SSLServerSocket server = (SSLServerSocket) factory.createServerSocket(_SSL_PORT);
			
			System.out.printf("Echo (server) started on %d\n", _SSL_PORT);
			
			for (;;) {
				try (SSLSocket client = (SSLSocket) server.accept()) {
					try (BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
						String line = null;
						while ((line = input.readLine()) != null) {
							System.out.printf("-> Echo (server): %s\n", line);
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
