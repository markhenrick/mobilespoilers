package site.markhenrick.mobilespoilers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MobileSpoilersTest {
	@Test
	void cannotStopStoppedInstance() throws Exception {
		var ms = new MobileSpoilers(Config.loadFromYaml("config.example.yaml"));
		assertThat(ms.isRunning()).isFalse();
		assertThrows(IllegalStateException.class, ms::stop);
	}
}
