/**
 * 
 */
package org.dongq.analytics.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dongq.analytics.utils.DbHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;

/**
 * @author eastseven
 *
 */
public class QuestionnaireLoginComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(QuestionnaireLoginComposer.class);
	
	Textbox responderNo;
	Textbox responderPwd;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.info(comp);
	}
	
	void init() {
		
	}
	
	//登录
	public void onClick$submitBtn() {
		String no = responderNo.getValue();
		String pwd = responderPwd.getValue();
		
		QueryRunner query = new QueryRunner();
		Connection conn = DbHelper.getConnection();
		final String check = "select a.version v, a.responder_pwd pwd, a.responder_id id from responder a where a.responder_no = ? and a.responder_pwd = ?";
		
		logger.debug(check);
		try {
			Map<String, Object> map = query.query(conn, check, new MapHandler(), no, pwd);
			if(map != null && !map.isEmpty()) {
				logger.debug(map);

				
				String uri = "/eastseven?m=login";
				uri = "demo.zul";
				
				this.execution.setAttribute("no", no);
				this.execution.setAttribute("pwd", pwd);
				this.execution.sendRedirect(uri);
			} else {
				this.alert("编号或密码错误");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/*
	public void onClick$resetBtn() {
		this.responderNo.setValue(null);
		this.responderPwd.setValue(null);
	}
	 */
}
