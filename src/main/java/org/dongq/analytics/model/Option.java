/**
 * 
 */
package org.dongq.analytics.model;

/**
 * @author eastseven
 * 
 */
public class Option {

	private long key;

	private String value;

	private int version;

	public Option() {
		super();
	}

	public Option(int key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return this.key + "." + this.value;
	}
}
