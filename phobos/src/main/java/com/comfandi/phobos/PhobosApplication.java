package com.comfandi.phobos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(scanBasePackages = "com.comfandi")
// @EnableScheduling // Habilita la ejecución de tareas programadas
@SpringBootApplication
public class PhobosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhobosApplication.class, args);
	}

}
