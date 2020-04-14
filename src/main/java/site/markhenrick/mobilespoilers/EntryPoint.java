package site.markhenrick.mobilespoilers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;

public final class EntryPoint {
	private static final Logger LOG = LoggerFactory.getLogger(EntryPoint.class);

	private EntryPoint() { }

	public static void main(String... args) throws LoginException, IOException, IllegalAccessException {
		LOG.info("Starting with args {}", Arrays.toString(args));

		if (args.length > 1) {
			System.err.println("Usage: mobilespoilers [config yaml path]");
			System.exit(1);
		}

		var configPath = args.length == 0 ? "config.yaml" : args[0];
		LOG.info("Loading config from {}", configPath);
		var config = Config.loadFromYaml(configPath);
		new MobileSpoilers(config).start();
	}
}
