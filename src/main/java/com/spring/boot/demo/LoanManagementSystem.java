package com.spring.boot.demo;

import com.spring.boot.demo.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LoanManagementSystem {

//	Until you build a /login endpoint, you can verify PasswordEncoder.matches() using a quick JUnit test or a temporary CommandLineRunner bean in your main application class:
	@Bean
	CommandLineRunner verifyPasswords(AppUserRepository repo, PasswordEncoder encoder) {
		return args -> {
			String hash1 = repo.findByEmail("rahul@gmail.com").orElseThrow().getPassword();
			String hash2 = repo.findByEmail("rahul.verma@gmail.com").orElseThrow().getPassword();

			System.out.println("User 1 matches raw password: " + encoder.matches("Rahul@123", hash1)); // true
			System.out.println("User 2 matches raw password: " + encoder.matches("Rahul@123", hash2)); // true
			System.out.println("User 1 matches wrong password: " + encoder.matches("WrongPassword", hash1));    // false
		};
	}

	public static void main(String[] args) {

		SpringApplication.run(LoanManagementSystem.class, args);
	}

}
