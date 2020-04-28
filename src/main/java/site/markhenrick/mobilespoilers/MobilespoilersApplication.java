package site.markhenrick.mobilespoilers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import site.markhenrick.mobilespoilers.discord.services.JDAHolder;

@SpringBootApplication
public class MobilespoilersApplication {

	@SuppressWarnings("resource")
	public static void main(String... args) {
		SpringApplication.run(MobilespoilersApplication.class, args);
	}

	public MobilespoilersApplication(JDAHolder jdaHolder) {
		this.jdaHolder = jdaHolder;
	}

	private final JDAHolder jdaHolder;

	@Profile("!test")
	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> jdaHolder.start();
	}

}
