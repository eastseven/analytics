package org.dongq.analytics.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dongq.analytics.utils.DbHelper;
import org.zkforge.fckez.FCKeditor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

/**
 * 
 * @author eastseven
 * 
 */
public class FckEditorComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 7081177505002279922L;

	private static final Log logger = LogFactory.getLog(FckEditorComposer.class);

	private FCKeditor fckeditor;
	private Object version;

	@SuppressWarnings("rawtypes")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Map map = arg;
		version = map.get("version");
		logger.debug("current selected version is : " + version);
		
		Connection conn = DbHelper.getConnection();
		PreparedStatement ps = conn.prepareStatement("select version, title from questionnaire_title where version = " + version);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {  
			String content = getDerbyClobContent(rs.getClob("title"));
			fckeditor.setValue(content);
		}
		
		DbUtils.close(rs);
		DbUtils.close(ps);
		DbUtils.close(conn);
	}

	public void onClick$saveBtn() {
		String content = fckeditor.getValue();

		logger.debug("current selected version is : " + version);
		logger.debug("current fckeditor value is : \n" + content);

		Connection conn = DbHelper.getConnection();
		String insert = "insert into questionnaire_title values (?,?)";
		String delete = "delete from questionnaire_title where version = " + version;
		String sql = insert;
		try {

			PreparedStatement ps = conn.prepareStatement(delete);
			ps.execute();
			conn.commit();

			ps = conn.prepareStatement(sql);
			ps.setObject(1, version);
			ps.setAsciiStream(2, derbyAsciiStream(content));
			int record = ps.executeUpdate();
			conn.commit();

			logger.debug(sql + " : " + record);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				DbUtils.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (self instanceof Window) {
			Window win = (Window) self;
			win.onClose();
		}
	}

	private ByteArrayInputStream derbyAsciiStream(String content) throws Exception {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(b, "UTF-8"));
		w.write(content);
		w.close();
		return new ByteArrayInputStream(b.toByteArray());
	}

	private String getDerbyClobContent(Clob derbyClob) throws Exception {
		BufferedInputStream in = new BufferedInputStream(derbyClob.getAsciiStream());
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(bs);
		byte[] ioBuf = new byte[4096];
		int bytesRead;
		while ((bytesRead = in.read(ioBuf)) != -1) out.write(ioBuf, 0, bytesRead);
		out.close();
		in.close();
		return new String(bs.toString());
	}
}
