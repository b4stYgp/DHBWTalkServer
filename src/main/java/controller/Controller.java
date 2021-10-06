package controller;

import java.sql.SQLException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.FreundesAnfrage;
import models.Student;

@RestController
@RequestMapping("/student")
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
	
}
