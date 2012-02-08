package org.dongq.analytics.service;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;

public interface QuestionnairePaperService {

	public static final String TYPE_OPEN  = "open";
	public static final String TYPE_CLOSE = "close";
	
	public boolean hasAnswered(long responderId);
	
	public Responder login(String no, String pwd);
	
	public String getQuestionnaireTitle(Object version);
	
	public List<Responder> getRespondersOfVersion(long version);
	
	public Questionnaire getQuestionnaire(long responderId);
	
	public Questionnaire getBlankQuestionnaire(long version);
	
	public List<Question> getQuestionsOfVersion(long version, int type, Long responderId) throws SQLException;
	
	public boolean saveQuestionnairePaper(QuestionnairePaper paper);
	
	public boolean saveQuestionnairePaper(Responder responder, Map<String, Object> answer);
	
	public boolean parseQuestionnaireTemplate(InputStream excel, final String type);
	
	public Object[][] calculate(long version);
	
	public Map<Object, Object[][]> calculateForMatrix(long version);
	
	public Workbook generateExcelForQuestionnaire(long version);
	
	public Workbook generateExcelForQuestionnaireMatrixNet(long version);
	
	public Workbook generateExcelForQuestionnaire(Object[][] normalQuestion, Map<Object, Object[][]> matrixQuestion);
}
