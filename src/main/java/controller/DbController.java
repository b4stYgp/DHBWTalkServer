package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import models.FreundesAnfrage;
import models.Student;

public class DbController {
	private final String url = "jdbc:mysql://localhost:3306/";
	private final String user = "root";
	private final String password = "";
	private final String datenbank = "DHBWServer";
	private Connection connec;
	
	public DbController()
	{		
	}
	
	private void baueVerbindung() throws SQLException, ClassNotFoundException
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connec = DriverManager.getConnection(url+datenbank,user,password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean registriereStudent(Student student) throws SQLException, ClassNotFoundException
	{
		baueVerbindung();
		String query = "INSERT INTO `student`(`id`, `name`, `surname`, `gender`, `courseID`) VALUES ('"
				+ student.getId()
				+ "','" + student.getName()
				+ "','" + student.getSurname() 
				+ "','" + student.getGender() 
				+ "','" + student.getCourseID() 
				+ "')";
		Statement stmt = connec.createStatement();
		int ergebnis = stmt.executeUpdate(query);
		stmt.close();
		connec.close();
		if(ergebnis == 1) return true;
		else return false;
	}

	/* public List<String> bekommeStudenten(FreundesAnfrage anfrage) {
		baueVerbindung();
		String query = ""
		
	}*/
}
