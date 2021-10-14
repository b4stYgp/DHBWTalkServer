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

import models.Freund;
import models.Student;

@RestController
@RequestMapping("/students")
public class Controller {
	
	@PostMapping("/")
	public String registriereStudent(@RequestHeader("matrikelnummer") String matrikelnummer, @RequestHeader("authorization") String passwort, @RequestBody Student student) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		student.setMatrikelnummer(matrikelnummer);		
		student.setPasswort(passwort);				
		return dbController.registriereStudent(student) ? "Student angelegt" : "Student abgewiesen";
	}
	
	@GetMapping("/")
	public String anmeldenStudent(@RequestHeader("matrikelnummer") String matrikelnummer, @RequestHeader("authorization") String passwort) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();		
		return dbController.anmelden(matrikelnummer, passwort);
	}
	
	@GetMapping("/{name}/{surname}/{courseID}")
	public String erhalteFriendOptions(@PathVariable ("name") String name, @PathVariable ("surname") String surname, @PathVariable ("courseID") String courseID) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		String studenten = dbController.bekommeStudenten(name, surname, courseID);
		return studenten;		
	}	

	
	@PostMapping("/{matrikelnummer}/freunde")
	public String anfrageFreundschaft(@RequestBody Freund anfrage,  @PathVariable("matrikelnummer") String matrikelnummer ) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();		
		return dbController.anfrageFreundschaft(anfrage, matrikelnummer) ? "Freundesanfrage gesendet" : "Freundschaftsanfrage abgelehnt";	
	}
	
	
	@GetMapping("/{matrikelnummer}/offenefreunde")
	public String bekommeOffeneFreundschaftsanfragen(@PathVariable ("matrikelnummer") String matrikelnummer) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bekommeOffeneFreundschaftsanfragen(matrikelnummer);
	}
	
	@GetMapping("/{matrikelnummer}/freunde")
	public String bekommeFreunde(@PathVariable ("matrikelnummer") String matrikelnummer) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bekommeFreunde(matrikelnummer);
	}
	

	@PutMapping("/{matrikelnummer}/freunde")
	public String bestaetigeFreundschaftsAnfrage(@RequestBody Freund anfrage, @PathVariable("matrikelnummer") String matrikelnummer) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bestaetigeFreundschaftsAnfrage(anfrage, matrikelnummer) ? "Freundschaft bestaetigt" : "Freundschaft abegelehnt" ;		
	}
	
}
