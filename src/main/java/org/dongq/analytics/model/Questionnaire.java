/**
 * 
 */
package org.dongq.analytics.model;

import java.util.List;

/**
 * @author eastseven <br/>
 */
public class Questionnaire {

	private Responder responder;

	private List<QuestionGroup> group;

	private List<Question> matrix;

	private List<Responder> people;

	private List<ResponderProperty> properties;

	public Questionnaire() {

	}

	public Responder getResponder() {
		return responder;
	}

	public void setResponder(Responder responder) {
		this.responder = responder;
	}

	public List<QuestionGroup> getGroup() {
		return group;
	}

	public void setGroup(List<QuestionGroup> group) {
		this.group = group;
	}

	public List<Question> getMatrix() {
		return matrix;
	}

	public void setMatrix(List<Question> matrix) {
		this.matrix = matrix;
	}

	public List<Responder> getPeople() {
		return people;
	}

	public void setPeople(List<Responder> people) {
		this.people = people;
	}

	public List<ResponderProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ResponderProperty> properties) {
		this.properties = properties;
	}

}
