package main;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import controller.Controller;

@SpringBootApplication
@ComponentScan(basePackageClasses = Controller.class)
public class Main {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Main.class);
		app.setDefaultProperties(Collections
		          .singletonMap("server.port", "8083"));
		        app.run(args);
	}

}
