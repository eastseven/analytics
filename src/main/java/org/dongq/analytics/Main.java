package org.dongq.analytics;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

	final static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	final static String url = "jdbc:derby:sample;create=true";

	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {

//		Class.forName(driver);
//
//		Connection conn = DriverManager.getConnection(url);
//
//		if (conn != null) {
//			System.out.println("connecting... : " + conn);
//			conn.close();
//			System.out.println("connection is close");
//		}

		Base.open(driver, url, null);
		
		String drop = "DROP TABLE people";
		String create  = "CREATE TABLE people (name VARCHAR(56) NOT NULL,last_name VARCHAR(56), createTime TIME)";
		try {
			Base.exec(drop);
		} catch (Exception e) {
			logger.info("people表不存在，将自动创建");
		}
		Base.exec(create);
		
		Base.close();
		logger.info("done...");
		
		InputStream input = new FileInputStream("/Users/eastseven/Desktop/网络数据1-26-交流频率已反向.xls");
		POIFSFileSystem fs = new POIFSFileSystem(input);  
		HSSFWorkbook wb = new HSSFWorkbook(fs);  
		int numberOfSheets = wb.getNumberOfSheets();
		logger.info("Number Of Sheets "+numberOfSheets);
		HSSFSheet sheet = null;
		for(int index = 0; index < numberOfSheets; index++) {
			sheet = wb.getSheetAt(index);
			logger.info(sheet.getSheetName());
		}
	}

}
