package site.markhenrick.mobilespoilers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import site.markhenrick.mobilespoilers.discord.service.JDAHolder;

@SpringBootApplication
public class MobilespoilersApplication {

	@SuppressWarnings("resource")
	public static void main(String... args) {
		SpringApplication.run(MobilespoilersApplication.class, args);
	}

	public MobilespoilersApplication(JDAHolder jdaFacade) {
		this.jdaFacade = jdaFacade;
	}

	private final JDAHolder jdaFacade;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> jdaFacade.start();
	}

}
