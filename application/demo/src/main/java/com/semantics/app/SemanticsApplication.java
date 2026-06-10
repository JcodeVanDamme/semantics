package com.semantics.app;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SemanticsApplication {
	public static void main(String[] args) {
		SpringApplication.run(SemanticsApplication.class, args);
	}
}