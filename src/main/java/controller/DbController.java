package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Freund;
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
		query = "INSERT INTO `student`(`name`, `surname`, `gender`, `courseID`, `matrikelnummer`, `passwort` ) VALUES ('"				
				+  student.getName()
				+ "','" + student.getSurname() 
				+ "','" + student.getGender() 
				+ "','" + student.getCourseID() 
				+ "','" + korrigierePasswortString(student.getMatrikelnummer())
				+ "','" + student.getPasswort()
				+ "')";
		stmt = connec.createStatement();
		int ergebnis = stmt.executeUpdate(query);
		stmt.close();		
		connec.close();
		if(ergebnis == 1) return true;
		else return false;
	}
	
	public String anmelden(String matrikelnummer, String passwort) throws ClassNotFoundException, SQLException
	{
		query = "SELECT `id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummer + "\" AND `passwort` = \"" + korrigierePasswortString(passwort) + "\"";
		baueVerbindung();
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);
		if(rs.next()) return "true";
		else return "false";
	}
	
	private String korrigierePasswortString(String passwort) {
		String korrigiertesPasswort = passwort.substring(5,passwort.length());
		return korrigiertesPasswort;
	}

	public String bekommeStudenten(String name, String surname, String courseID) throws ClassNotFoundException, SQLException {		
		baueVerbindung();
		Freund anfrage = new Freund(name, surname, courseID);
		vorbereitenFreundesQuery(anfrage);
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);
		String studentenJSON = vorbereitenStudentenListe();
		stmt.close();
		rs.close();
		connec.close();
		return studentenJSON;
	}
	
	private void vorbereitenFreundesQuery(Freund anfrage)
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
		
		switch(anfrage.getMatrikelnummer())
		{
		case "null" : break;
		default : if(hatVorgaenger) {query = query.concat(" AND ");} query = query.concat("`matrikelnummer` = \"" + anfrage.getMatrikelnummer() + "\""); break;
		}		
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
			studenten = studenten.concat("\"matrikelnummer\":");
			studenten = studenten.concat("\"" + rs.getString("matrikelnummer")+ "\"");
			studenten = studenten.concat("},");			
		}
		studenten = studenten.substring(0, studenten.length()-1);
		studenten = studenten.concat("]");			
		return studenten;
	}

	
	public boolean anfrageFreundschaft(Freund anfrage, String matrikelnummer) throws ClassNotFoundException, SQLException {
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
		//Mit dieser Id wird auf der FriendTabelle abgefragt ob Studenten anfragen gestellt haben
		if(student2Id.equals("kein Student mit der Matrikelnummer gefunden")) return student2Id;
		stmt.close();
		String freundesAnfragen = leseFreundesAnfragen(student2Id);
		stmt.close();
		connec.close();
		return freundesAnfragen;
	}


	private String bekommeStudent2Id(String matrikelnummer) throws SQLException {
		query = "SELECT `Id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummer + "\"";
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);		
		if(rs.next())
		{
			return rs.getString("Id");			
		}
		else return "kein Student mit der Matrikelnummer gefunden";
	}

	private String leseFreundesAnfragen(String student2Id) throws SQLException {
		query = "SELECT `Student1_Id` FROM `friendlist` WHERE `Student2_Id` = "
				+ student2Id + " AND `isFriend` = 0";
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);  //Liste an StudentenId die auf Freundschaftsanfrage warten
		List<String> studentenIds = new ArrayList<String>();		
		while(rs.next())
		{
			studentenIds.add(rs.getString("Student1_Id"));
		}
		if(!studentenIds.isEmpty())
		{
			query = "SELECT `name`, `surname`, `courseID` FROM `student` WHERE ";
			
			for(int i=0; i < studentenIds.size();i++)
			{
				query = query.concat("`id` = "+ studentenIds.get(i));
				query = query.concat(" OR ");
			}
			query = query.substring(0, query.length()-4);		
			rs = stmt.executeQuery(query);
			return vorbereitenStudentenListe();	
		}
		return "";		
	}


	public boolean bestaetigeFreundschaftsAnfrage(Freund anfrage, String matrikelnummer) throws ClassNotFoundException, SQLException {
		query = "UPDATE `friendlist` SET `isFriend`= 1 WHERE `Student2_Id` = (SELECT `id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummer + "\""
				+ ") AND `Student1_Id` = (SELECT `id` FROM `student` WHERE `name` = \"" + anfrage.getName() + "\""
				+ " AND `surname` = \"" + anfrage.getSurname() + "\"  AND `courseID` = \"" + anfrage.getCourseID() + "\")";
		baueVerbindung();
		stmt = connec.createStatement();
		int reihen = stmt.executeUpdate(query);
		if(reihen == 1)return true;
		else return false;
	}

	public String bekommeFreunde(String matrikelnummer) throws ClassNotFoundException, SQLException {
		query = "SELECT `id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummer + "\"";
		baueVerbindung();
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);
		String id = "";
		String freunde = "";
		if(rs.next())
		{
			id = rs.getString("id");
			query = "SELECT `Student1_Id`, `Student2_Id` FROM `friendlist` WHERE (`Student1_Id` = \"" + id + "\" OR `Student2_Id` = \"" + id + "\") AND `isFriend` = 1";
		}
		else return "keine Freunde";
		rs = stmt.executeQuery(query);
		List<String> ids = new ArrayList<String>();
		while(rs.next())
		{		
			if(id.equals(rs.getString("Student1_Id"))) ids.add(rs.getString("Student2_Id"));
			else ids.add(rs.getString("Student1_id"));
		}		
		if(ids.stream().count() != 0)
		{
			erstelleBekommeFreundeQuery(ids);
			rs = stmt.executeQuery(query);
			freunde = vorbereitenStudentenListe();
		}		
		stmt.close();
		connec.close();
		return freunde;
	}


	private void erstelleBekommeFreundeQuery(List<String> ids) {
		query = "SELECT `name`, `surname`, `courseID` , `matrikelnummer` FROM `student` WHERE ";
		for(int i=0; i < ids.size(); i++)
		{
			query = query.concat("`id` = \"" + ids.get(i) + "\"");
			query = query.concat(" OR ");			
		}
		query = query.substring(0, query.length()-4);		
	}	 
}
