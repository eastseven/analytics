/**
 * 
 */
package org.dongq.analytics.model;

import java.util.Set;

/**
 * @author eastseven<br/>
 * 
 */
public class Responder {

	private long id;

	private String name;

	private long version;

	private Set<ResponderProperty> properties;

	public Responder() {
		super();
	}

	public Responder(long id, String name, long version,
			Set<ResponderProperty> properties) {
		super();
		this.id = id;
		this.name = name;
		this.version = version;
		this.properties = properties;
	}

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

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Set<ResponderProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<ResponderProperty> properties) {
		this.properties = properties;
	}

}
