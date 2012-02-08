/**
 * 
 */
package org.dongq.analytics.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		
		if("admin".equals(no) && "000000".equals(pwd)) {
			session.setAttribute("no", no);
			execution.sendRedirect("index.zul");
		} else {
			alert("账号或密码错误");
		}
		
		
	}
	
	public void onOK$responderPwd() {
		onClick$submitBtn();
	}
}
