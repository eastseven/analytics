package org.dongq.analytics.model;

import java.util.ArrayList;
import java.util.List;

public class OptionGroup {

	private long id;

	private String name;

	private List<Option> options = new ArrayList<Option>();

	private long version;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getRadio() {
		String html = "";
		if (this.options != null && !this.options.isEmpty()) {
			for(Option o : this.options) {
				html += "<tr><td><input type='radio' name='property_a' value='"+o.getId()+"' />" + o.getDisplay() + "</td></tr>";
			}
		}
		return html;
	}
	
	@Override
	public String toString() {
		return "OptionGroup [id=" + id + ", name=" + name + ", options="
				+ options + ", version=" + version + "]";
	}

}
