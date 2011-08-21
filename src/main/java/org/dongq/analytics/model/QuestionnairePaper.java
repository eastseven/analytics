/**
 * 
 */
package org.dongq.analytics.model;

import java.util.Date;
import java.util.Map;

/**
 * @author eastseven
 * 
 */
public class QuestionnairePaper {

	private long id;

	private Questionnaire questionnaire;

	private Responder responder;

	private Date date;

	private Map<Long, Integer> answers;

	public QuestionnairePaper() {
		this.date = new Date();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	public Responder getResponder() {
		return responder;
	}

	public void setResponder(Responder responder) {
		this.responder = responder;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Map<Long, Integer> getAnswers() {
		return answers;
	}

	public void setAnswers(Map<Long, Integer> answers) {
		this.answers = answers;
	}

}
