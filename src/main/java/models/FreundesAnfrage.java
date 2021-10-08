package models;

public class FreundesAnfrage {
	private String name;
	private String surname;
	private String courseID;
	
	
	public FreundesAnfrage(String name, String surname, String courseID)
	{
		setName(name);
		setSurname(surname);
		setCourseID(courseID);
	}
	
	public String getName() {
		return name;
	}	
	
	public void setName(String name) {
		this.name = name;
	}	
	
	public String getSurname() {
		return surname;
	}	
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getCourseID() {
		return courseID;
	}
	
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
}
