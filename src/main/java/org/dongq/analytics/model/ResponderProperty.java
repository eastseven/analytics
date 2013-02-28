package org.dongq.analytics.model;

/**
 * 
 * @author eastseven
 * 
 */
public class ResponderProperty {

	private String id;

	private String name;

	private String display;

	private int value;

	private long version;

	public ResponderProperty() {
	}

	public ResponderProperty(String id) {
		this.id = id;
	}

	public ResponderProperty(String id, String name, String display, int value,
			long version) {
		super();
		this.id = id;
		this.name = name;
		this.display = display;
		this.value = value;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
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
		return "ResponderProperty [id=" + id + ", name=" + name + ", display="
				+ display + ", value=" + value + ", version=" + version + "]";
	}

}
