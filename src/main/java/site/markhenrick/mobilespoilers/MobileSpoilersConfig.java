package site.markhenrick.mobilespoilers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "mobilespoilers")
@ToString
public class MobileSpoilersConfig {
	private static final Logger LOG = LoggerFactory.getLogger(MobileSpoilersConfig.class);

	@Getter
	@Setter
	private String token;

	@Getter
	@Setter
	private String deletionEmoji;

	@Getter
	@Setter
	private String prefix;

	@Getter
	@Setter
	private String adminUserId;

	@Getter
	@Setter
	private boolean showAdminInfo;

	@PostConstruct
	public void postConstruct() {
		LOG.info("Loaded config {}", this);
	}
}
