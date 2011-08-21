/**
 * 
 */
package org.dongq.analytics.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author eastseven <br/>
 *         问卷；调查情况用的一组问题<br/>
 */
public class Questionnaire {

	private long id;

	/**
	 * 问题集<br/>
	 */
	private List<QuestionGroup> questions;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<QuestionGroup> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionGroup> questions) {
		this.questions = questions;
		
	}

	public List<Question> getAllQuestions() {
		List<Question> list = new ArrayList<Question>();
		if(this.getQuestions() != null && !this.getQuestions().isEmpty()) {
			Iterator<QuestionGroup> iter = this.getQuestions().iterator();
			while(iter.hasNext()) {
				QuestionGroup group = iter.next();
				list.addAll(group.getQuestions());
			}
		}
		return list;
	}
	
	public String text() {
		String text = "";
		if (this.questions != null && !this.questions.isEmpty()) {
			int indexGroup = 1;
			for (QuestionGroup group : this.questions) {
				text += indexGroup + "." + group.getTitle() + " :\n";
				int index = 1;
				for (Question question : group.getQuestions()) {
					text += index + "." + question + "\n";
					index++;
				}
				indexGroup++;
			}
		}
		return text;
	}

	public void print() {
		System.out.println(text());
	}

}
