/**
 * 
 */
package org.dongq.analytics.model;

/**
 * @author eastseven<br/>
 *         �ܷ��߻�����Ϣ
 */
public class Responder {

	private long id;

	private String name;
	private String company;
	private String gender;
	private String marriage;
	private int age;
	private String education;// �����̶�
	private String discipline;// ѧ��

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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMarriage() {
		return marriage;
	}

	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getDiscipline() {
		return discipline;
	}

	public void setDiscipline(String discipline) {
		this.discipline = discipline;
	}

}
