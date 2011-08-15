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

	/**
	 * 正确选项
	 */
	int rightOption;

	public Question() {
		super();
	}

	public Question(String content, List<Option> options, int rightOption) {
		super();
		this.content = content;
		this.options = options;
		this.rightOption = rightOption;
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

	public int getRightOption() {
		return rightOption;
	}

	public void setRightOption(int rightOption) {
		this.rightOption = rightOption;
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
