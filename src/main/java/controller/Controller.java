package controller;

import java.sql.SQLException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.Anfrage;
import models.Student;


//API zur Verwaltung von Studenten in einer Datenbank


@RestController
@RequestMapping("/students") 
public class Controller {
	
	
	//Post-Method für Neuanlage eines "Student"-Datensatzes in der Datenbank
	/* 
	 * Erhält: 	 Header: matrikelnummer, passwort	
	 * 			 Body:   Student Objekt im JSON Format ohne matrikelnummer und passwort
	 * Return:: "Student angelegt" oder "Student abgewiesen";
	 */
	@PostMapping("/") 
	public String registriereStudent(@RequestHeader("matrikelnummer") String matrikelnummer, @RequestHeader("authorization") String passwort, @RequestBody Student student) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		student.setMatrikelnummer(matrikelnummer); 					//Student vervollständigen,
		student.setPasswort(passwort);								//da im Body die matrikelnummer und das passwort nicht enthalten sind.
		return dbController.registriereStudent(student) ? "Student angelegt" : "Student abgewiesen";
	}
	
	
	//Get-Methode zur Überprüfung ob ein Datensatz mit gegebener matrikelnummer und passwort in der Tabelle Student der Datenbank vorhanden ist
	/*
	 *  Erhält:	 Header: matrikelnummer,passwort
	 *  Return:: "true" oder "false"
	 */
	@GetMapping("/")
	public String anmeldenStudent(@RequestHeader("matrikelnummer") String matrikelnummer,
								  @RequestHeader("authorization") String passwort) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.anmelden(matrikelnummer, passwort);
	}
	
	
	//Get-Methode um den Student mit der matrikenummer zu erhalten
	/*
	 * Erhält:   PathVariable: matrikelnummer
	 * Return: 	String mit, für den Client aufbereitete, Student-Objekte im JSON Format oder Leerstring :
	 * 			"{"name":"name","surname":"nachname","courseID":"TINF18","matrikelnummer":"1234567"}"
	 * 			oder ""
	 */
	@GetMapping("/{matrikelnummer}")
	public String bekommeStudent(@PathVariable("matrikelnummer") String matrikelnummer) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bekommeStudent(matrikelnummer);		
	}
	
	
	//Get-Methode für die Suche nach Freunden
	/*
	 *  Erhält:	  Header	  : matrikelnummer
	 *  		  PathVariable: Name, Nachname, KursId
	 *  Return: String mit Liste an, für den Client aufbereitete, StudentObjekten im JSON Format oder einen Leerstring:
	 *  		"[{"name":"name","surname":"nachname","courseID":"TINF18","matrikelnummer":"1234567"}]"
	 *  		oder ""  						
	 */
	@GetMapping("/{name}/{surname}/{courseID}")
	public String erhalteFriendOptions(@RequestHeader("matrikelnummer") String matrikelnummer,
									   @PathVariable ("name") String name,
									   @PathVariable ("surname") String surname,
									   @PathVariable ("courseID") String courseID) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		String studenten = dbController.bekommeStudenten(name, surname, courseID, matrikelnummer);
		return studenten;
	}

	
	
	//Post-Methode um eine Freundschaft zu initialisieren (Freundschaftsanfrage senden)
	/*
	 * Erhält:	 PathVariable: matrikelnummer
	 * 			 Body		 : Freund Objekt im JSON Format
	 * Return:: "Freundschaftsanfrage eingetragen" oder "Freundschaftsanfrage nicht eingetragen"
	 */
	@PostMapping("/{matrikelnummer}/freunde")
	public String anfrageFreundschaft(@PathVariable("matrikelnummer") String matrikelnummer,
									  @RequestBody Anfrage anfrage) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.anfrageFreundschaft(anfrage, matrikelnummer) ? "Freundschaftsanfrage eingetragen" : "Freundschaftsanfrage nicht eingetragen";
	}

	
	
	//Get-Methode um offene Freunschaftschaftsanfragen zu erhalten
	/*
	 * Erhält:   PathVariable: matrikelnummer
	 * Return:: String mit Liste an, für den Client aufbereitete, Student-Objekten im JSON Format oder Leetstring :
	 * 			"[{"name":"name","surname":"nachname","courseID":"TINF18","matrikelnummer":"1234567"}]"
	 *			oder ""
	 */
	@GetMapping("/{matrikelnummer}/offenefreunde")
	public String bekommeOffeneFreundschaftsanfragen(@PathVariable ("matrikelnummer") String matrikelnummer) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bekommeOffeneFreundschaftsanfragen(matrikelnummer);
	}
	
	
	
	//Put Methode um Freundschaftsanfragen zu bestätigen, also initialisierte Freundschaften zu updaten
	/*
	 * Erhält:   PathVariable: matrikelnummer
	 * 			 Body		 : Freund Objekt im JSON-Format	
	 * Returend: "Freundschaft bestaetigt" oder "Freundschaft abegelehnt"
	 */
	@PutMapping("/{matrikelnummer}/freunde")
	public String bestaetigeFreundschaftsAnfrage(@PathVariable("matrikelnummer") String matrikelnummer,@RequestBody Anfrage anfrage) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bestaetigeFreundschaftsAnfrage(anfrage, matrikelnummer) ? "Freundschaft bestaetigt" : "Freundschaft abegelehnt" ;
	}
	
	//Get-Methode um Freunde bekommen
	/*
	 * Erhalte:		PathVariable: matrikelnummer
	 * Return:	String mit Liste an StudentObjekten im JSON Format oder Leerstring :
	 * 			"[{"name":"testname","surname":"testnachname","courseID":"TINF18","matrikelnummer":"1234567"}]"
	 * 			oder ""
	 */
	@GetMapping("/{matrikelnummer}/freunde")
	public String bekommeFreunde(@PathVariable ("matrikelnummer") String matrikelnummer) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bekommeFreunde(matrikelnummer);
	}




}