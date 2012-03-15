/**
 * 
 */
package org.dongq.analytics.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author eastseven
 * 
 */
public class DbHelper {

	final static Log logger = LogFactory.getLog(DbHelper.class);
	
	static String driver = "org.apache.derby.jdbc.ClientDriver";
	static String url = "jdbc:derby://localhost:1527/analytics;create=true;upgrade=true";
	static String user = "analytics";
	static String password = "analytics";
	
	static {
		DbUtils.loadDriver(driver);
	}
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
