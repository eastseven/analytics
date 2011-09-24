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
	
	public static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public static String url = "jdbc:derby:database";
	
	static {
		/*
		try {
			Configuration config = new PropertiesConfiguration(new File("src/main/resources/jdbc.properties"));
			driver = config.getString("db.driver");
			url = config.getString("db.url");
			url = url.replace(config.getString("db.name"), config.getString("db.path")).replace("true", "false");
		} catch (ConfigurationException e) {
			//e.printStackTrace();
			logger.info("读取jdbc配置文件失败");
		}
		
		 */
		logger.debug(driver);
		logger.debug(url);
		DbUtils.loadDriver(driver);
	}
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			logger.debug(url);
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
