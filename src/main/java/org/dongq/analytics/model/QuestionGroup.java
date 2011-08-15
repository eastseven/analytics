/**
 * 
 */
package org.dongq.analytics.model;

import java.util.List;

/**
 * @author eastseven
 * 
 */
public class QuestionGroup {

	String title;

	List<Question> group;

	public QuestionGroup(String title, List<Question> group) {
		super();
		this.title = title;
		this.group = group;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Question> getGroup() {
		return group;
	}

	public void setGroup(List<Question> group) {
		this.group = group;
	}

}
