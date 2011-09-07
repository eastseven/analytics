/**
 * 
 */
package org.dongq.analytics.model;

/**
 * @author eastseven
 * 
 */
public class Option {
	private long id;
	private int key;
	private String value;
	private long version;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Option [id=" + id + ", key=" + key + ", value=" + value
				+ ", version=" + version + "]";
	}

}
