/**
 * 
 */
package org.dongq.analytics.model;

import java.util.List;

/**
 * @author eastseven <br/>
 *         问卷；调查情况用的一组问题<br/>
 */
public class Questionnaire {

	/**
	 * 问题集<br/>
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
