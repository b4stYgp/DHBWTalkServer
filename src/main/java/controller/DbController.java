package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	
	
	private void baueVerbindung() throws SQLException, ClassNotFoundException
	{		
			Class.forName("com.mysql.cj.jdbc.Driver");		
			connec = DriverManager.getConnection(url+datenbank,user,password);		
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
		vorbereitenFreundesQuery(anfrage);
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);
		String studentenJSON = vorbereitenStudentenListe();
		stmt.close();
		rs.close();
		connec.close();
		return studentenJSON;
	}
	
	private void vorbereitenFreundesQuery(FreundesAnfrage anfrage)
	{
		boolean hatVorgaenger = false;
		query = "SELECT `id`, `name`, `surname`, `gender`, `courseID`, `matrikelnummer` FROM `student` WHERE ";
		
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

	
	public boolean anfrageFreundschaft(FreundesAnfrage anfrage, String matrikelnummer) throws ClassNotFoundException, SQLException {
		baueVerbindung();
		vorbereitenFreundesQuery(anfrage);
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);
		boolean bool = speichereFreundschaftsAnfrage(matrikelnummer);
		stmt.close();		
		connec.close();
		return bool;
	}	
	
	private boolean speichereFreundschaftsAnfrage(String matrikelnummer) throws SQLException {
		if(rs.next())
		{
			String freundId = rs.getString("id");
			rs.close();		
			query = "SELECT `id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummer + "\"";
			stmt = connec.createStatement();
			rs = stmt.executeQuery(query);
			if(rs.next())
			{
				String studentId = rs.getString("id");
				rs.close();		
				query = "INSERT INTO `friendlist`(`Student1_Id`, `Student2_Id`, `isFriend`) VALUES ('"
						+ studentId + "','" 
						+ freundId +  "','"
						+ 0 + "')";
				int ergebnis = stmt.executeUpdate(query);		
				if(ergebnis == 1) return true;
				else return false;
			}
			else return false;
		}
		else return false;				
	}
	
	public String bekommeOffeneFreundschaftsanfragen(String matrikelnummer) throws ClassNotFoundException, SQLException {
		//bekomme Id des Studenten welccher überpfrüfen will ob Freundesanfragen vorhanden sind
		baueVerbindung();
		String student2Id = bekommeStudent2Id(matrikelnummer);		
		//
		
		stmt.close();
		connec.close();
		return null;
	}


	private String bekommeStudent2Id(String matrikelnummer) throws SQLException {
		query = "SELECT `Student1_Id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummer + "\"";
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);		
		if(rs.next())
		{
			return rs.getString("Student1_Id");			
		}
		else return "kein Student mit der Matrikelnummer gefunden";
	}





	public boolean bestaetigeFreundschaftsAnfrage(FreundesAnfrage anfrage, String matrikelnummer) {
		// TODO Auto-generated method stub
		return false;
	}




	 
}
