package com.world.worldproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class WorldProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorldProxyApplication.class, args);
	}

	@RestController
	@RequestMapping(path = "")
	public static class WelcomeController {

		@GetMapping("")
		String welcome() {
			return "Welcome to World proxy Service";
		}
	}

}
