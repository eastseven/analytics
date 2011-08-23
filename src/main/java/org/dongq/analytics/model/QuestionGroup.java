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

	private long id;

	private String title;

	private List<Question> questions;

	private List<Option> options;

	public QuestionGroup() {
		super();
	}

	public QuestionGroup(String title, List<Question> group) {
		super();
		this.title = title;
		this.questions = group;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public List<Option> getOptions() {
		return options;
	}

	public String getOptionsDescribe() {
		String describe = "";

		if (this.options != null && !this.options.isEmpty()) {
			for (Option option : this.options) {
				describe += option.getKey() + "=" + option.getValue() + ",";
			}
		}

		return describe;
	}

	public void setOptions(List<Option> options) {
		this.options = options;

		if (this.questions != null && !this.questions.isEmpty()) {
			for (Question q : this.questions) {
				q.setOptions(options);
			}
		}
	}

}
