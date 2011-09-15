/**
 * 
 */
package org.dongq.analytics.ui;

import java.io.FileNotFoundException;

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
	}
	
	public void onClick$upload() {
		Component comp = execution.createComponents("questionnaire_upload.zul", main, null);
		logger.debug(comp);
		if (comp instanceof Window) {
			Window win = (Window) comp;
			try {
				win.doModal();
			} catch (SuspendNotAllowedException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * 下载
	 */
	public void onClick$download() {
		logger.info(application.getRealPath("/"));
		logger.info(application.getResourcePaths("/"));
		try {
			Filedownload.save(this.application.getResource("/template.xls"), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//
	public void onClick$open() {
		mainDiv.getChildren().clear();
		logger.debug("opening..." + mainDiv + ":" + mainDiv.getId());
		Component comp = execution.createComponents("questionnaire_grid.zul", mainDiv, null);
		logger.debug(comp);
	}

	public void onClick$answer() {
		execution.sendRedirect("questionnaire_login.zul", "blank");
	}
	
	public void onClick$about() {
		this.alert(Version.RELEASE);
	}
}
