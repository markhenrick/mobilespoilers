package site.markhenrick.mobilespoilers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class Config {
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	private String token;
	private String dbPath;
	private String prefix;
	private String reaction;
	private String adminUserId;
	private Boolean showAdminInfo;

	public String getToken() {
		return token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(final String dbPath) {
		this.dbPath = dbPath;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(final String reaction) {
		this.reaction = reaction;
	}

	public String getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(final String adminUserId) {
		this.adminUserId = adminUserId;
	}

	public Boolean isShowAdminInfo() {
		return showAdminInfo;
	}

	public void setShowAdminInfo(final Boolean showAdminInfo) {
		this.showAdminInfo = showAdminInfo;
	}

	@Override
	public String toString() {
		try {
			final var sb = new StringBuilder("{\n");
			for (final var field : Config.class.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					sb.append('\t').append(field.getName()).append(": ").append(field.get(this)).append(",\n");
				}
			}
			sb.append("}");
			return sb.toString();
		} catch (final IllegalAccessException e) {
			return "Unable to render config to string - reflective access denied";
		}
	}

	public void validate() throws IllegalAccessException {
		var passed = true;
		for (final var field : Config.class.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()) && field.get(this) == null) {
				LOG.error("Please set the {} field in your config YAML", field.getName());
				passed = false;
			}
		}
		if (!passed) {
			throw new IllegalArgumentException("Not all config properties have been set. See error logging above");
		}
	}

	public static Config loadFromYaml(final String filepath) throws IOException, IllegalAccessException {
		final var yaml = new Yaml(new Constructor(Config.class));
		final var file = new File(filepath);
		LOG.info("Loading config from {}", file);
		try (
			final var stream = new FileInputStream(file)
		) {
			final var config = (Config) yaml.load(stream);
			LOG.info("Loaded config: {}", config);
			config.validate();
			return config;
		}
	}
}
