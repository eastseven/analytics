package org.dongq.analytics.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.dongq.analytics.service.QuestionnairePaperService;
import org.dongq.analytics.service.QuestionnairePaperServiceImpl;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Filedownload;

public class ExcelGenerateEventListener implements EventListener {

	private static final Log logger = LogFactory.getLog(ExcelGenerateEventListener.class);
	
	public static final int TYPE_1 = 1;
	public static final int TYPE_2 = 2;
	
	private Long fileName;
	
	private int type;
	
	private QuestionnairePaperService service;
	
	public ExcelGenerateEventListener(Long fileName, int type) {
		this.fileName = fileName;
		this.type = type;
		
		this.service = new QuestionnairePaperServiceImpl();
	}
	
	@Override
	public void onEvent(Event event) throws Exception {

		Execution exe = event.getPage().getDesktop().getExecution();
		String path = exe.getServerName() + ":" + exe.getServerPort() + exe.getContextPath();
		logger.debug(path);
		try {
			File file = new File(fileName + ".xls");
			logger.debug(file.getAbsolutePath());
			FileOutputStream out = FileUtils.openOutputStream(file);
			FileInputStream in = FileUtils.openInputStream(file);
			String contentType = "application/vnd.ms-excel";
			QuestionnairePaperService service = new QuestionnairePaperServiceImpl();
			Workbook wb = this.type == TYPE_1 ? service.generateExcelForQuestionnaire(fileName) : service.generateExcelForQuestionnaireMatrixNet(fileName);
			wb.write(out);
			out.close();
			
			Filedownload.save(in, contentType, fileName + "-"+this.type+".xls");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		
	}

}
