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

	private List<Question> matrixNet;

	private List<Responder> people;

	private List<OptionGroup> optionGroups;

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

	public List<Question> getMatrixNet() {
		return matrixNet;
	}

	public void setMatrixNet(List<Question> matrixNet) {
		this.matrixNet = matrixNet;
	}

	public List<Responder> getPeople() {
		return people;
	}

	public void setPeople(List<Responder> people) {
		this.people = people;
	}

	public List<OptionGroup> getOptionGroups() {
		return optionGroups;
	}

	public void setOptionGroups(List<OptionGroup> optionGroups) {
		this.optionGroups = optionGroups;
	}

}
