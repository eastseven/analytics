/**
 * 
 */
package org.dongq.analytics.model;

/**
 * @author eastseven
 * 
 */
public class QuestionnairePaper {

	private String responderId;

	private String questionId;

	private long optionKey;

	private int type;

	private long version;

	private String finishTime;

	public String getResponderId() {
		return responderId;
	}

	public void setResponderId(String responderId) {
		this.responderId = responderId;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public long getOptionKey() {
		return optionKey;
	}

	public void setOptionKey(long optionKey) {
		this.optionKey = optionKey;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	@Override
	public String toString() {
		return "QuestionnairePaper [responderId=" + responderId
				+ ", questionId=" + questionId + ", optionKey=" + optionKey
				+ ", type=" + type + ", version=" + version + ", finishTime="
				+ finishTime + "]";
	}

}
