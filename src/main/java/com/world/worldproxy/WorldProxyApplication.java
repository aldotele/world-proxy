package com.world.worldproxy;

import com.world.worldproxy.persistence.WorldDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@Slf4j
public class WorldProxyApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(WorldProxyApplication.class, args);
		WorldDB worldDB = new WorldDB();
		worldDB.init();
	}

	@RestController
	@RequestMapping(path = "")
	public static class WelcomeController {

		@GetMapping("")
		String welcome() {
			return "Welcome to World Proxy service";
		}
	}
}

