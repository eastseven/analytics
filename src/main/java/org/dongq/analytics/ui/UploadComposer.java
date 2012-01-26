/**
 * 
 */
package org.dongq.analytics.ui;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dongq.analytics.service.QuestionnairePaperService;
import org.dongq.analytics.service.QuestionnairePaperServiceImpl;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * @author eastseven
 * 
 */
public class UploadComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(UploadComposer.class);
	
	private final String FILE_SUFFIX = "xls,xlsx";
	
	private QuestionnairePaperService service;
	private String type;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		service = new QuestionnairePaperServiceImpl();
		logger.debug(comp);
		logger.debug(arg.get("type"));
		
		type = (String)arg.get("type");
		String title = "open".equals(type) ? "开放式问卷上传" : "封闭式问卷上传";
		
		Window win = (Window)comp;
		win.setTitle(title);
	}
	
	/**
	 * 上传
	 * @param event
	 */
	public void onUpload$file(UploadEvent event) {
		Media file = event.getMedia();
		if(FILE_SUFFIX.contains(file.getFormat().toLowerCase())) {
			InputStream excel = file.getStreamData();
			boolean bln = service.parseQuestionnaireTemplate(excel, type);
			String msg = bln ? "问卷导入成功" : "问卷导入失败";
			try {
				Messagebox.show(msg, "导入结果", Messagebox.OK, "", new EventListener() {
					@Override
					public void onEvent(Event event) throws Exception {
						event.getPage().getDesktop().getExecution().sendRedirect("index.zul");
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
			}
		} else {
			alert("非excel格式文件不能上传");
		}
	}
}
