package org.dongq.analytics;

import java.io.File;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.ddlutils.task.DdlToDatabaseTask;
import org.apache.ddlutils.task.WriteSchemaToDatabaseCommand;
import org.dongq.analytics.utils.DbHelper;

public class CreateDatabase {

	public static void main(String[] args) throws Exception {
		System.out.println("start...");
		Properties p = new Properties();
		p.put("driverClassName", DbHelper.driver);
		p.put("url", "jdbc:derby://58.215.190.80/analytics;create=true");
		p.put("username", DbHelper.user);
		p.put("password", DbHelper.password);
		BasicDataSource dataSource = (BasicDataSource)BasicDataSourceFactory.createDataSource(p);
		
		DdlToDatabaseTask task = new DdlToDatabaseTask();
		task.setSchemaFile(new File(System.getProperties().getProperty("user.dir") + "/src/main/resources/project-schema.xml"));
		task.addConfiguredDatabase(dataSource);
		task.addWriteSchemaToDatabase(new WriteSchemaToDatabaseCommand());
		task.execute();
		
		System.out.println("done...");
	}

}
