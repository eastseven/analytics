/**
 * 
 */
package org.dongq.analytics.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author eastseven<br/>
 * 
 */
public class Responder {

	private long id;

	private String name;

	private long version;

	private String no;

	private String pwd;

	private Set<ResponderProperty> properties = new HashSet<ResponderProperty>();

	public Responder() {
		super();
	}

	public Responder(long id, String name, long version, Set<ResponderProperty> properties) {
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

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public String toString() {
		return "Responder [id=" + id + ", name=" + name + ", version="
				+ version + ", no=" + no + ", pwd=" + pwd + ", properties="
				+ properties + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Responder other = (Responder) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
