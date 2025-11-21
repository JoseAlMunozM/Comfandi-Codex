package com.comfandi.korlon;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication(scanBasePackages = "com.comfandi.korlon")
public class KorlonApplication {

	public static void main(String[] args) {
		SpringApplication.run(KorlonApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		try {
			return args -> {

				System.out.println("Let's inspect the number beans provided by Spring Boot:");
				String[] beanNames = ctx.getBeanDefinitionNames();
				System.out.print(" #" + beanNames.length + " Beans Loaded!");
			};
			
		} catch (Exception e) {
			System.err.println(e);
			return args ->{
				System.out.println("Error!!!" + e.getMessage());
			};
		}
	}
}
