package main.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(
	    servers = {
	        @Server(url = "/", description = "Default Server URL"),
	        @Server(url = "/filestoring", description = "File Storing API")
	    }
	)
@SpringBootApplication
public class FileStoringApplication {
	public static void main(String args[])
	{
		SpringApplication.run(FileStoringApplication.class, args);
	}
}
