package main;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import controller.Controller;

@SpringBootApplication
@ComponentScan(basePackageClasses = Controller.class)
public class Main {

	public static void main(String[] args) throws URISyntaxException, IOException { // both exceptions are a result of tests -> will be removed
		SpringApplication app = new SpringApplication(Main.class);
		app.setDefaultProperties(Collections
		          .singletonMap("server.port", "8083"));
		        app.run(args);

		test("http://localhost:8083/heartbeat","Alex", "passwort1!");
		test("http://localhost:8083/heartbeat","Sven", "MothersMaidenName");
		test("http://localhost:8083/add/Alex","Sven", "MothersMaidenName");
		test("http://localhost:8083/add/Sven","Sven", "MothersMaidenName");
		test("http://localhost:8083/add/Sven","Sven", "MothersMaidenName");
		test("http://localhost:8083/rooms","Alex", "passwort1!");
		test("http://localhost:8083/join/0","Alex", "passwort1!");

		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			Desktop.getDesktop().browse(new URI(test("http://localhost:8083/url","Alex", "passwort1!")));
			// open BBB on windows
		}
		else
		{
		Runtime rt = Runtime.getRuntime();
		String url = test("http://localhost:8083/url","Alex", "passwort1!");
		rt.exec("open " + url);
		}
		// open BBB on mac

	}

	// Tests
	// This function is the BBB interface -> pass url and user credentials and server will take it from there
	static String test(String urlString, String username, String password)
	{
		CloseableHttpClient client = HttpClients.createDefault();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		HttpGet httpGet = new HttpGet(urlString);
		try {
			httpGet.addHeader(new BasicScheme().authenticate(credentials, httpGet, null) );
			// custom header for login credentials is necessary -> HTML has no default 'secure' header
			httpGet.addHeader("username", username);
		} catch (AuthenticationException e){System.out.println(e);}
		try {
			CloseableHttpResponse response = client.execute(httpGet);
			// httpGet response as string
			client.close();
			return EntityUtils.toString(response.getEntity());
		}
		catch (ClientProtocolException e) {System.out.println(e);}
		catch (IOException e) {System.out.println(e);}
		return "";
	}

}
