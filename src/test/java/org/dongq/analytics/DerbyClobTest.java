package org.dongq.analytics;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.dongq.analytics.utils.DbHelper;

public class DerbyClobTest {

	public static void main(String[] args) throws Exception {

		Connection conn = DbHelper.getConnection();
		Long version = System.currentTimeMillis();
		String sql = "insert into questionnaire_title values (?,?)";
		String content = "简体中文abc";
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(b,"UTF-8"));
		w.write(content);
		w.close();
		ByteArrayInputStream derbyAsciiStream = new ByteArrayInputStream(b.toByteArray());
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setObject(1, version);
		ps.setAsciiStream(2, derbyAsciiStream);
		int record = ps.executeUpdate();
		conn.commit();
		System.out.println("record : " + record);
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("select title from questionnaire_title");
		while (rs.next()) {
			java.sql.Clob aclob = rs.getClob(1);
			System.out.println(aclob.getSubString(1, (int)aclob.length()));
			java.io.InputStream ip = aclob.getAsciiStream();
			int c = ip.read();
			while (c > 0) {
				System.out.print((char) c);
				c = ip.read();
			}
			System.out.print("\n");
		}
	}

}
