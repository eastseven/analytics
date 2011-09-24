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

	private String display;

	private String value;

	private long version;

	private boolean selected;

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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	@Override
	public String toString() {
		return "Option [id=" + id + ", key=" + key + ", display=" + display
				+ ", value=" + value + ", version=" + version + ", selected="
				+ selected + "]";
	}

}
