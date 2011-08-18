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

	private int id;

	private Questionnaire questionnaire;

	private Responder responder;

	private Date date;

	private Map<Integer, Integer> answers;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public Map<Integer, Integer> getAnswers() {
		return answers;
	}

	public void setAnswers(Map<Integer, Integer> answers) {
		this.answers = answers;
	}

}
