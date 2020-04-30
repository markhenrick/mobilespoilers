package site.markhenrick.mobilespoilers;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "mobilespoilers")
@Data
public class MobileSpoilersConfig {
	private static final Logger LOG = LoggerFactory.getLogger(MobileSpoilersConfig.class);

	private String token;
	private String deletionEmoji;
	private String prefix;
	private String adminUserId;
	private boolean showAdminInfo;
	private boolean statistics;

	@PostConstruct
	public void postConstruct() {
		LOG.info("Loaded config {}", this);
	}
}
