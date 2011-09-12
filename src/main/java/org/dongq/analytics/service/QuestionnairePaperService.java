package org.dongq.analytics.service;

import java.io.InputStream;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;

public interface QuestionnairePaperService {

	public Questionnaire getQuestionnaire(long responderId);
	
	public Questionnaire getBlankQuestionnaire(long version);
	
	public boolean saveQuestionnairePaper(QuestionnairePaper paper);
	
	public boolean saveQuestionnairePaper(Responder responder, Map<String, Object> answer);
	
	public boolean parseQuestionnaireTemplate(InputStream excel);
	
	public Object[][] calculate(long version);
	
	public Map<Object, Object[][]> calculateForMatrix(long version);
	
	public Workbook generateExcelForQuestionnaire(long version);
	
	public Workbook generateExcelForQuestionnaire(Object[][] normalQuestion, Map<Object, Object[][]> matrixQuestion);
}
