package org.dongq.analytics;

import java.net.InetAddress;

import org.apache.derby.drda.NetworkServerControl;

public class DerbyServerTest {

	public static void main(String[] args) {
		System.out.println("start...");
		try {
			NetworkServerControl server = new NetworkServerControl(InetAddress.getByName("127.0.0.1"), 19527);
			server.start(null);
			//server.main(new String[] {"start"});
			System.out.println("derby server running...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finish...");
	}

}
