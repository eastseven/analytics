/**
 * 
 */
package org.dongq.analytics.ui;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.Version;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

/**
 * @author eastseven
 * 
 */
public class IndexComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(IndexComposer.class);
	
	Div mainDiv;
	
	Window main;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug(comp + ":" + comp.getId());
		logger.debug(mainDiv + ":" + mainDiv.getId());
		
		//check login account
		Object user = session.getAttribute("no");
		if(user == null) {
			Window loginWin = (Window)execution.createComponents("login.zul", comp, null);
			loginWin.setMode("modal");
			main.appendChild(loginWin);
		} else {
			
		}
	}
	
	/**
	 * 上传封闭式问卷
	 */
	@SuppressWarnings("unchecked")
	public void onClick$uploadClose() {
		arg = new HashMap<String, String>();
		arg.put("type", "close");
		Component comp = execution.createComponents("questionnaire_upload.zul", main, arg);
		logger.debug(comp);
		if (comp instanceof Window) {
			Window win = (Window) comp;
			try {
				win.doModal();
			} catch (SuspendNotAllowedException e) {
				//e.printStackTrace();
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void onClick$uploadOpen() {
		arg = new HashMap<String, String>();
		arg.put("type", "open");
		Component comp = execution.createComponents("questionnaire_upload.zul", main, arg);
		logger.debug(comp);
		if (comp instanceof Window) {
			Window win = (Window) comp;
			try {
				win.doModal();
			} catch (SuspendNotAllowedException e) {
				//e.printStackTrace();
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * 下载封闭式问卷模板
	 */
	public void onClick$downloadClose() {
		logger.info(application.getRealPath("/"));
		logger.info(application.getResourcePaths("/"));
		try {
			Filedownload.save(this.application.getResource("/template.xls"), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 下载开放式问卷模板
	 */
	public void onClick$open() {
		try {
			Filedownload.save(this.application.getResource("/templateOpen.xls"), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void onClick$about() {
		this.alert(Version.RELEASE);
	}
}
