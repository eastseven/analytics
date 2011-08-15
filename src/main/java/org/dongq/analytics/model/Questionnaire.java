/**
 * 
 */
package org.dongq.analytics.model;

import java.util.List;

/**
 * @author eastseven <br/>
 *         �ʾ���������õ�һ������<br/>
 */
public class Questionnaire {

	/**
	 * ���⼯<br/>
	 */
	List<Question> questions;

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	void print() {
		if (this.questions != null && !this.questions.isEmpty()) {
			int index = 1;
			for(Question question : this.questions) {
				System.out.println(index+"."+question.toString()+"\n");
				index++;
			}
		}
	}

}
