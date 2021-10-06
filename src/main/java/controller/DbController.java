package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import models.FreundesAnfrage;
import models.Student;

public class DbController {
	private final String url = "jdbc:mysql://localhost:3306/";
	private final String user = "root";
	private final String password = "";
	private final String datenbank = "DHBWServer";
	private String query;
	private Connection connec;
	private Statement stmt;
	private ResultSet rs;
	
	public DbController()
	{		
	}
	

	public boolean registriereStudent(Student student) throws SQLException, ClassNotFoundException
	{
		baueVerbindung();
		query = "INSERT INTO `student`(`name`, `surname`, `gender`, `courseID`, `matrikelnummer` ) VALUES ('"				
				+  student.getName()
				+ "','" + student.getSurname() 
				+ "','" + student.getGender() 
				+ "','" + student.getCourseID() 
				+ "','" + student.getId()
				+ "')";
		stmt = connec.createStatement();
		int ergebnis = stmt.executeUpdate(query);
		stmt.close();
		connec.close();
		if(ergebnis == 1) return true;
		else return false;
	}

	public String bekommeStudenten(FreundesAnfrage anfrage) throws ClassNotFoundException, SQLException {		
		baueVerbindung();
		vorbereitenFreundeQuery(anfrage);
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);
		String studentenJSON = vorbereitenStudentenListe();
		stmt.close();
		connec.close();
		return studentenJSON;
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
	
	
	private void vorbereitenFreundeQuery(FreundesAnfrage anfrage)
	{
		boolean hatVorgaenger = false;
		query = "SELECT `name`, `surname`, `gender`, `courseID`, `matrikelnummer` FROM `student` WHERE ";
		switch(anfrage.getName())
		{
		case "null" : break;
		default : query = query.concat("`name` = \"" + anfrage.getName() + "\""); hatVorgaenger = true; break;
		}
		
		switch(anfrage.getSurname())
		{
		case "null" : break;
		default : if(hatVorgaenger) {query = query.concat(" AND ");} query = query.concat("`surname` = \"" + anfrage.getSurname() + "\""); break;
		}
		switch(anfrage.getCourseID())
		{
		case "null" : break;
		default : if(hatVorgaenger) {query = query.concat(" AND ");} query = query.concat("`courseID` = \"" + anfrage.getCourseID() + "\""); break;
		}
		System.out.println(query);
	}
	
	private String vorbereitenStudentenListe() throws SQLException
	{
		String studenten = "[";
		while(rs.next())
		{
			studenten = studenten.concat("{");
			studenten = studenten.concat("\"name\":");
			studenten = studenten.concat("\"" + rs.getString("name") + "\",");
			studenten = studenten.concat("\"surname\":");
			studenten = studenten.concat("\"" + rs.getString("surname")+ "\",");
			studenten = studenten.concat("\"courseID\":");
			studenten = studenten.concat("\"" + rs.getString("courseID")+ "\"");
			studenten = studenten.concat("},");					
		}
		studenten = studenten.substring(0, studenten.length()-1);
		studenten = studenten.concat("]");
		System.out.println(studenten);
		return studenten;
	}
	 
}
