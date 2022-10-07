package ru.yandex.practicum.filmorate;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class FilmorateApplication {

	public static void main(String[] args) {
		// to kill process: kill $(cat ./bin/shutdown.pid)
		SpringApplicationBuilder app = new SpringApplicationBuilder(FilmorateApplication.class);
		app.build().addListeners(new ApplicationPidFileWriter("./bin/shutdown.pid"));
		app.run(args);
	}

}
