package controller;

import java.sql.SQLException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.FreundesAnfrage;
import models.Student;

@RestController
@RequestMapping("/students")
public class Controller {
	
	@PostMapping("/")
	public String registriereStudent(@RequestBody Student student) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();		
		return dbController.registriereStudent(student) ? "Student angelegt" : "Student abgewiesen";
	}	
	
	@GetMapping("/")
	public String erhalteFriendOptions(@RequestBody FreundesAnfrage anfrage) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		String studenten = dbController.bekommeStudenten(anfrage);
		return studenten;		
	}
	
	@GetMapping("/{matrikelnummer}/offenefreunde")
	public String bekommeOffeneFreundschaftsanfragen(@PathVariable ("matrikelnummer") String matrikelnummer) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bekommeOffeneFreundschaftsanfragen(matrikelnummer);
	}
	
	@PostMapping("/{matrikelnummer}/offenefreunde")
	public String anfrageFreundschaft(@RequestBody FreundesAnfrage anfrage,  @PathVariable("matrikelnummer") String matrikelnummer ) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();		
		return dbController.anfrageFreundschaft(anfrage, matrikelnummer) ? "Freundesanfrage gesendet" : "Freundschaftsanfrage abgelehnt, weil schon gesendet";	
	}
	
	@PutMapping("/{matrikelnummer}/offenefreunde")
	public String bestaetigeFreundschaftsAnfrage(@RequestBody FreundesAnfrage anfrage, @PathVariable("matrikelnummer") String matrikelnummer) throws ClassNotFoundException, SQLException
	{
		DbController dbController = new DbController();
		return dbController.bestaetigeFreundschaftsAnfrage(anfrage, matrikelnummer) ? "Freundschaft bestaetigt" : "Freundschaft abegelehnt" ;		
	}
	
}
