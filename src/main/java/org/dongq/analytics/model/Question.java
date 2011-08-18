/**
 * 
 */
package org.dongq.analytics.model;

import java.util.List;

/**
 * @author eastseven
 * 
 */
public class Question {

	/**
	 * 问题
	 */
	String content;

	/**
	 * 选项集合
	 */
	List<Option> options;

	public Question() {
		super();
	}

	public Question(String content) {
		super();
		this.content = content;
	}

	public Question(List<Option> options, String content) {
		super();
		this.content = content;
		this.options = options;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		String optionString = "";
		for (Option option : this.options) {
			optionString += "  " + option.toString();
		}
		return this.content + "\n" + optionString;
	}
}
