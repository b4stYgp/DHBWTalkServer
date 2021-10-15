package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Anfrage;
import models.Student;


//Klasse für Verwaltung der Datenbank
/*
 * 			Datenbankinformationen
 * 		Tabelle 		Spalten
 * 		student 		(id(P)[Auto_Increment], name, surname, gender, courseID, matrikelnummer(unique), passwort)
 * 		friendlist		(id(P)[Auto_Increment], Student1_Id(student.id), Student2_Id(student.id), isFriend) 		
 *		
 *
 *		
 */
public class DbController {
	
	//Attribute
	private final String url = "jdbc:mysql://localhost:3306/";
	private final String user = "root";
	private final String password = "";
	private final String datenbank = "DHBWServer";
	private String query;
	private Connection connec;
	private Statement stmt;
	private ResultSet rs;
	
	//Konstruktor
	public DbController()
	{
	}
	
	//Verbindung zur Datenbank aufbauen
	private void baueVerbindung() throws SQLException, ClassNotFoundException
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		connec = DriverManager.getConnection(url+datenbank,user,password);
	}
	
	
	//registriereStudent ist zuständig für die Anlegung eines neuen Datensatzes in der "Student" Tabelle
	/*
	 * 	Parameter: Student-Objekt
	 * 	Return   : bool - true or false
	 * 
	 */
	public boolean registriereStudent(Student student) throws SQLException, ClassNotFoundException
	{
		baueVerbindung();	//wirft ClassNotFoundException und SQL Exception
		query = "INSERT INTO `student`(`name`, `surname`, `gender`, `courseID`, `matrikelnummer`, `passwort` ) VALUES ('"
				+  student.getName()
				+ "','" + student.getSurname()
				+ "','" + student.getGender()
				+ "','" + student.getCourseID()
				+ "','" + student.getMatrikelnummer()
				+ "','" + korrigierePasswortString(student.getPasswort())
				+ "')";
		stmt = connec.createStatement();  			// wirft SQLException
		int ergebnis = stmt.executeUpdate(query);   /* Ergebnis enthält nach Ausführung die Anzahl an Reihen die vom Query betroffen waren. Bei korrekter Nutzung 1.
													   wirft SQLException*/
		stmt.close();								// wirft SQLException
		connec.close();								// wirft SQLException		
		if(ergebnis == 1) return true; 		
		else return false;
	}
	
	
	//anmelden ist zuständig um zu überprüfen ob ein Datensatz zu der Kombination matrikelnummer und passwort in der Tabelle Student vorhanden ist
	/*
	 *  Parameter: String (matrikelnummer der Quelle), String (passwort der Quelle)
	 *  Return   : String - "true" oder "false"
	 */
	public String anmelden(String matrikelnummer, String passwort) throws ClassNotFoundException, SQLException
	{
		query = "SELECT `id` FROM `student` WHERE `matrikelnummer` = \""
					+ matrikelnummer
					+ "\" AND `passwort` = \"" + korrigierePasswortString(passwort) + "\"";
		baueVerbindung();
		stmt = connec.createStatement();		// wirft SQLException (In nachfolgenden Methoden nicht erneut kommentiert) 
		rs = stmt.executeQuery(query);			// wirft SQLException (In nachfolgenden Methoden nicht erneut kommentiert)
		if(rs.next())
		{			
			stmt.close();						// wirft SQLException
			connec.close();						// wirft SQLException
			return "true";			
		}
		else
		{			
			stmt.close();						// wirft SQLException
			connec.close();						// wirft SQLException (In nachfolgenden Methoden nicht erneut Kommentiert)
			return "false";
		}
	}
	
	
	//korrigierePasswortString kürzt den String am Anfang um das Wort "Basic "
	/*
	 *  Parameter: String Bsp:("Basic test")
	 *  Return   : String Bsp:("test")
	 */
	private String korrigierePasswortString(String einString) {
		return einString.substring(5,einString.length());		
	}
	
	
	//bekommeStudent sucht einen Datensatz in der Tabelle "student" und gib die benötigten Informationen zu einem Student zurück
	/*
	 * Return: String mit einem, für den Client und die Übertragung verkürztem, Student-Objekt im JSON Format oder Leerstring :
	 * "{"name":"testname","surname":"testnachname","courseID":"TINF18","matrikelnummer":"1234567"}" oder ""
	 */
	public String bekommeStudent(String matrikelnummer) throws ClassNotFoundException, SQLException {
		query = "SELECT * FROM `student` WHERE `matrikelnummer`= \"" + matrikelnummer + "\"";
		baueVerbindung();
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);
		String student = vorbereitenStudentenListe();
		stmt.close();
		connec.close();
		if(!student.equals(""))
			return student.substring(1,student.length()-1);
		else return student;
	}
	
	//bekommeStudenten sucht einen Datensatz in der Tabelle "student" und gibt die Informationen zu allen Student-Objekten zurück
	/*
	 *  Parameter: name,nachname,KursId (Suchkriterien), matrikelnummer (der Quelle der Anfrage)
	 *  Return:	String mit Liste an, für den Client und die Übertragung verkürzten, Student-Objekten im JSON Format oder einen Leerstring:
	 *    "[{"name":"testname","surname":"testnachname","courseID":"TINF18","matrikelnummer":"1234567"}]"  oder "" 
	 */
	public String bekommeStudenten(String name, String surname, String courseID, String matrikelnummerClient) throws ClassNotFoundException, SQLException {
		baueVerbindung();
		Anfrage anfrage = new Anfrage(name, surname, courseID);
		vorbereitenFreundesQuery(anfrage, matrikelnummerClient);
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);
		System.out.println(query);
		String studentenJSON = vorbereitenStudentenListe();
		stmt.close();
		rs.close();
		connec.close();
		return studentenJSON;
	}
	
	
	//vorbereitenFreundesQuey erzeugt aus einem Freund Objekt(ohne Matrikelnummer) und einer matrikelnummer ein SQL-Query zur Abfrage
	//nach allen Datensätzen auf die, die Anfrage-Attribute zutreffen
	/*
	 *  Parameter:	Anfrage, String (matrikelnummerder Quelle)
	 *  Return:		String mit SQL-Query 
	 *  			Bsp:
	 *  	"SELECT `id`, `name`, `surname`, `gender`, `courseID`, `matrikelnummer` FROM `student` WHERE
	 *  			`matrikelnummer` != "1234567" AND `surname` = "testnachname""		
	 */
	private void vorbereitenFreundesQuery(Anfrage anfrage, String matrikelnummerClient)
	{
		boolean hatVorgaenger = false;
		query = "SELECT `id`, `name`, `surname`, `gender`, `courseID`, `matrikelnummer` FROM `student` WHERE ";

		switch(matrikelnummerClient)
		{
			case "null" : break;
			default : query = query.concat("`matrikelnummer` != \"" + matrikelnummerClient + "\""); hatVorgaenger = true; break;
		}

		switch(anfrage.getName())
		{
			case "null" : break;
			default : if(hatVorgaenger) {query = query.concat(" AND ");} query = query.concat("`name` = \"" + anfrage.getName() + "\""); hatVorgaenger = true; break;
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
	}
	
	
	//vorbereitenStudentenListe erstellt aus dem im DbController gespeicherten ResultSet ein String im JSON Format
	// mit JSONObjekten für den Client.
	/*
	 * Return:  String im JSONFormat mit einer Liste and JSONObjekten für den Client
	 * 			Bsp: 
	 *			"[{"name":"testname","surname":"testnachname","courseID":"TINF18","matrikelnummer":"1234567"}]"
	 */
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
			studenten = studenten.concat("\"" + rs.getString("courseID")+ "\",");
			studenten = studenten.concat("\"matrikelnummer\":");
			studenten = studenten.concat("\"" + rs.getString("matrikelnummer")+ "\"");
			studenten = studenten.concat("},");
		}
		studenten = studenten.substring(0, studenten.length()-1);
		studenten = studenten.concat("]");
		if(studenten.equals("]"))
			return "";
		return studenten;
	}

	//anfrageFreundschaft erstellt aus einer Anfrage und einer matrikelnummer ein SQL Query und speichert die Anfrage in der Datenbenk
	/*
	 *  Parameter: Anfrage, String (matrikelnummer der Quelle)
	 *  Return: Boolean
	 */
	public boolean anfrageFreundschaft(Anfrage anfrage, String matrikelnummerClient) throws ClassNotFoundException, SQLException {
		baueVerbindung();
		vorbereitenFreundesQuery(anfrage, matrikelnummerClient);  //Select Query zum auslesen der ID passend der suchanfragen wird in query gespeichert
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);							  //eigene ID ist jetzt im ResultSet gespeichert
		boolean bool = speichereFreundschaftsAnfrage(matrikelnummerClient); //bool erhält das "Ergebnis" zur Speicherung der Anfrage in der Datenbank
		stmt.close();
		connec.close();
		return bool;
	}
	
	
	//speichereFreundschaftAnfrage erstellt ein Select-Query, um die zu der matrikelnummer gehörigen id auszulesen. Dann anhand des im DbController gespeicherten ResultSets
	//einen neuen Datensatz in der Tabelle "friendlist" einzutragen
	/*
	 * Parameter: String (matrikelnummer der Quelle)
	 * Return: Boolean
	 */
	private boolean speichereFreundschaftsAnfrage(String matrikelnummerClient) throws SQLException {
		if(rs.next()) //Im Result Set ist die id des freundes gespeichert
		{
			String freundId = rs.getString("id");
			rs.close();
			query = "SELECT `id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummerClient + "\"";
			stmt = connec.createStatement();
			rs = stmt.executeQuery(query);  //Nun ist im Result Set die id der Quelle gespeichert
			if(rs.next())
			{
				String studentId = rs.getString("id");
				rs.close();
				query = "INSERT INTO `friendlist`(`Student1_Id`, `Student2_Id`, `isFriend`) VALUES ('"
						+ studentId + "','"
						+ freundId +  "','"
						+ 0 + "')";
				int ergebnis = stmt.executeUpdate(query); 	// In der Tabelle "friendlist" wird ein Datensatz mit 0 generiert um eine Pending Freundschaftsanfgrage zu speichern
															// die studentId ist die id des Anfragenden, die friendId kann später über die Student2_Id in der friendlist abfragen
															// ob Pending-Requests vorhanden sind
				if(ergebnis == 1) return true;				// im ergebnis ist die Anzahl der betroffenen Spalten zurückgegeben, bei korrekter Nutzung 1. 
				else return false;
			}
			else return false;
		}
		else return false;
	}

	
	//bekommeOffeneFreunschaftsanfragen erstellt ein SQL-Query um eine id aus der Tabelle "student" auszulesen um mit dieser id in der friendlist nach offenen anfragen zu schauen
	/*
	 * Parameter: String (matrikelnummer der Quelle)
	 * Return: String im JSONFormat mit einer Liste and JSONObjekten für den Client
	 * 			Bsp: 
	 *			"[{"name":"testname","surname":"testnachname","courseID":"TINF18","matrikelnummer":"1234567"}]" 
	 */
	public String bekommeOffeneFreundschaftsanfragen(String matrikelnummerClient) throws ClassNotFoundException, SQLException {
		baueVerbindung();
		String student2Id = bekommeId(matrikelnummerClient);				//In student2Id ist die Id zur matrikelnummerClient gespeichert
		if(student2Id.equals("kein Student mit der Matrikelnummer gefunden")) return student2Id;
		stmt.close();
		String freundesAnfragen = leseFreundesAnfragen(student2Id);					//bekommt die Freundesanfragen als String im JSON-Format mit Liste zurück
		stmt.close();
		connec.close();
		return freundesAnfragen;
	}

	//bekommeStudent2Id erstellt ein SELECT-Query um die id aus der Tabelle "student" zu einer matrikelnummer zu finden.
	/*
	 * Parameter: String (matrikelnummerQuelle)
	 * Return:	String ("999") oder ("kein Student mit der Matrikelnummer gefunden")
	 */
	private String bekommeId(String matrikelnummerClient) throws SQLException {
		query = "SELECT `Id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummerClient + "\"";
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query); //Ergebnis der Abfrage im ResultSet gespeichert: Hier die gewünschte Id
		if(rs.next())		//Ist die Anfrage erfolgreich gewesen?
		{
			return rs.getString("Id");
		}
		else return "kein Student mit der Matrikelnummer gefunden";
	}
	
	
	//lesenFreundesAnfrage schaut in der Tabelle "friendlist" ob es Datensätze gibt bei denen die gegebene id in der Spalte isFriend eine 0 vorhanden ist
	//und somit überprüft ob es offene Freundeschaftsanfrage gibt
	/*
	 * Parameter: String (id der Quelle)
	 * Return:	String im JSONFormat mit einer Liste and JSONObjekten für den Client
	 * 		"[{"name":"testname","surname":"testnachname","courseID":"TINF18","matrikelnummer":"1234567"}]"
	 */
	private String leseFreundesAnfragen(String id) throws SQLException {
		query = "SELECT `Student1_Id` FROM `friendlist` WHERE `Student2_Id` = "
				+ id + " AND `isFriend` = 0";
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);  //Liste an StudentenId die auf Freundschaftsanfrage warten
		List<String> studentenIds = new ArrayList<String>();
		while(rs.next())			
		{
			studentenIds.add(rs.getString("Student1_Id"));			//speichere Ergebnis id's in Liste
		}
		if(!studentenIds.isEmpty())		//Liste leer?
		{
			query = "SELECT `name`, `surname`, `courseID` FROM `student` WHERE ";

			for(int i=0; i < studentenIds.size();i++)
			{
				query = query.concat("`id` = "+ studentenIds.get(i));
				query = query.concat(" OR ");
			}
			query = query.substring(0, query.length()-4);
			rs = stmt.executeQuery(query);					//Result Set enthält alle zutreffenden Datensätze
			return vorbereitenStudentenListe();				//übergebe einen String wie im "Return:" beschrieben
		}
		return "";
	}

	//bestaetigeFreundschaftsanfrage updated den Datensatz in der Tabelle "friendlist", zu dem die Angaben aus der Anfrage und die matrikelnummer(der Quelle) passen.
	//So dass dem Datensatz in der Spalte "isFriend" eine 1 zugewiesen wird.
	/*
	 * Parameter: Anfrage, String (matrikelnummer der Quelle)
	 * Return:	Boolean
	 */
	public boolean bestaetigeFreundschaftsAnfrage(Anfrage anfrage, String matrikelnummerClient) throws ClassNotFoundException, SQLException {
		query = "UPDATE `friendlist` SET `isFriend`= 1 WHERE `Student2_Id` = (SELECT `id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummerClient + "\""
				+ ") AND `Student1_Id` = (SELECT `id` FROM `student` WHERE `name` = \"" + anfrage.getName() + "\""
				+ " AND `surname` = \"" + anfrage.getSurname() + "\"  AND `courseID` = \"" + anfrage.getCourseID() + "\")";
		baueVerbindung();
		stmt = connec.createStatement();
		int reihen = stmt.executeUpdate(query);
		if(reihen == 1)return true;
		else return false;
	}

	
	//bekommeFreunde liest alle Freunde ids zur angegebenen Matrikelnummer durch die Tabelle "friendlist" aus und holt sich aus der Tabelle "student" die Informationen zu diesen Id,
	//danach wird ein String erstellt in dem im JSON Format alle für den API-Consumer relevanten Informationen in JSON Objekten gespeichert sind
	/*
	 * Parameter: String (matrikelnummer der Quelle)
	 * Return: String im JSONFormat mit einer Liste and JSONObjekten für den Client
	 * 		"[{"name":"testname","surname":"testnachname","courseID":"TINF18","matrikelnummer":"1234567"}]"
	 */
	public String bekommeFreunde(String matrikelnummerClient) throws ClassNotFoundException, SQLException {
		query = "SELECT `id` FROM `student` WHERE `matrikelnummer` = \"" + matrikelnummerClient + "\"";
		baueVerbindung();
		stmt = connec.createStatement();
		rs = stmt.executeQuery(query);		//Ergebnis der Abfrage im Result Set
		String id = "";
		String freunde = "";
		if(rs.next())		//existiert Datensatz zur matrikelnummer
		{
			id = rs.getString("id");		// Id des Clients gespeichert in id
			query = "SELECT `Student1_Id`, `Student2_Id` FROM `friendlist` WHERE (`Student1_Id` = \"" + id + "\" OR `Student2_Id` = \"" + id + "\") AND `isFriend` = 1";
		}
		else return "";//"keine Freunde"; TODO Dennis fragen wegen verarbeitung der Rückgabe
		rs = stmt.executeQuery(query); 
		List<String> ids = new ArrayList<String>();
		while(rs.next()) 
		{
			if(id.equals(rs.getString("Student1_Id"))) ids.add(rs.getString("Student2_Id"));		//Filtere die korrekte Id, da die Quell-Id in Spalte "Student1_Id"
			else ids.add(rs.getString("Student1_id"));												//und "Student2_Id" enthalten sein kann
		}
		if(ids.stream().count() != 0)		//Ist die Liste leer?
		{
			erstelleBekommeFreundeQuery(ids);	//speichert in query ein SELECT-Query, um die Informationen der Freunde aus der "student"-Tabelle zu lesen
			rs = stmt.executeQuery(query);
			freunde = vorbereitenStudentenListe();		//erstellt String wie im "Return:" beschrieben
		}
		stmt.close();
		connec.close();
		return freunde;
	}

	
	//erstlleBekommeFreundeQuery erzeugt ein Select Query um aus der Tabelle "student" alle Datensätze zu den in der Liste gespeicherten Ids zu erhalten
	/*
	 * Parameter List<String> (ids von Studierenden)
	 * Return: String (SQL Query)
	 * 		"SELECT `name`, `surname`, `courseID` , `matrikelnummer` FROM `student` WHERE `id` = "999" OR `id` = "998" OR `id` = "997"" 
	 */
	private void erstelleBekommeFreundeQuery(List<String> ids) {
		query = "SELECT `name`, `surname`, `courseID` , `matrikelnummer` FROM `student` WHERE ";
		for(int i=0; i < ids.size(); i++)
		{
			query = query.concat("`id` = \"" + ids.get(i) + "\"");
			query = query.concat(" OR ");
		}
		query = query.substring(0, query.length()-4); //Verkürze den String um die Länge des Strings " OR "
	}
	
	


}