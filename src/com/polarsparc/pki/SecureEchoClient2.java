/*
 *
 *  Name:        SecureEchoClient2
 *  Description: Echo client that uses the secure sockets to communicate with the secure echo server
 *               using insecure ciphers
 *  
 */

package com.polarsparc.pki;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SecureEchoClient2 {
	private static final int _SSL_PORT = 8443;
	private static final String _SSL_HOST = "localhost";
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.printf("Usage: java com.polarsparc.pki.SecureEchoClient2 <message>\n");
			System.exit(1);
		}
		
		try {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			
			SSLSocket socket = (SSLSocket) factory.createSocket(_SSL_HOST, _SSL_PORT);
			socket.setEnabledCipherSuites(socket.getSupportedCipherSuites()); // Insecure
			
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			output.write(args[0]+"\n");
			output.flush();
			
			socket.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
