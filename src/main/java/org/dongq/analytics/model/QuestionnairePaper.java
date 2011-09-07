/**
 * 
 */
package org.dongq.analytics.model;

/**
 * @author eastseven
 * 
 */
public class QuestionnairePaper {

	private long responderId;

	private long questionId;

	private long optionKey;

	private int type;

	private long version;

	private long finishTime;

	public long getResponderId() {
		return responderId;
	}

	public void setResponderId(long responderId) {
		this.responderId = responderId;
	}

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
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

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
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
