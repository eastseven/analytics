package org.dongq.analytics.service;

import java.io.InputStream;
import java.util.Map;

import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;

public interface QuestionnairePaperService {

	public Questionnaire getQuestionnaire(long responderId);
	
	public boolean saveQuestionnairePaper(QuestionnairePaper paper);
	
	public boolean saveQuestionnairePaper(Responder responder, Map<String, Object> answer);
	
	public boolean parseQuestionnaireTemplate(InputStream excel);
}