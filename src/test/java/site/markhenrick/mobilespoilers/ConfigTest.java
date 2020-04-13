package site.markhenrick.mobilespoilers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigTest {
	@Nested
	class Validation {
		@Test
		void validatesFieldsAreNotNull() {
			final var config = new Config();
			// All fields are null
			assertThrows(IllegalArgumentException.class, config::validate);
		}
	}

	// TODO use proper resource resolution instead of assuming cwd

	@Nested
	class ToString {
		private String output;

		@BeforeEach
		void setUp() throws IOException, IllegalAccessException {
			output = Config.loadFromYaml("config.example.yaml").toString();
		}

		@Test
		void rendersFields() {
			assertThat(output).contains("token").contains("sdofjowenerkfj");
		}

		@Test
		void doesNotincludeStatics() {
			assertThat(output).doesNotContain("LOG");
		}
	}

	@Nested
	class YamlLoad {
		@Test
		void loadsYaml() throws Exception {
			final var loadedConfig = Config.loadFromYaml("config.example.yaml");
			assertThat(loadedConfig.getToken()).isEqualTo("sdofjowenerkfj");
			assertThat(loadedConfig.getDbPath()).isEqualTo("spoilers.db");
			assertThat(loadedConfig.getPrefix()).isEqualTo("!");
//			assertThat(loadedConfig.getReaction()).isEqualTo("\uD83D\uDDD1️️"); // FIXME not working atm. Probably an encoding thing
			assertThat(loadedConfig.getAdminUserId()).isEqualTo("12345");
			assertThat(loadedConfig.isShowAdminInfo()).isEqualTo(true);
		}

		@Test
		void throwsOnInsufficientProperties() {
			final var path = "src/test/resources/configs/insufficient.yaml";
			assertThrows(Exception.class, () -> Config.loadFromYaml(path));
		}

		@Test
		void throwsOnExcessiveProperties() {
			final var path = "src/test/resources/configs/excessive.yaml";
			assertThrows(Exception.class, () -> Config.loadFromYaml(path));
		}

		@Test
		void throwsOnNullProperties() {
			final var path = "src/test/resources/configs/nulls.yaml";
			assertThrows(Exception.class, () -> Config.loadFromYaml(path));
		}
	}
}