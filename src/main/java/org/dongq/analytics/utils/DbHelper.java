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
	
	public static String driver = "org.apache.derby.jdbc.ClientDriver";
	public static String url = "jdbc:derby://127.0.0.1/analytics";
	public static String user = "analytics";
	public static String password = "analytics";
	
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
