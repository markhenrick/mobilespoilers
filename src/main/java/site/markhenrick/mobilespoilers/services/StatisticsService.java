package site.markhenrick.mobilespoilers.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;

import java.util.Arrays;

@SuppressWarnings("SqlWithoutWhere")
@Service
public class StatisticsService {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsService.class);

	private final MobileSpoilersConfig config;
	private final JdbcTemplate jdbcTemplate;

	public StatisticsService(MobileSpoilersConfig config, JdbcTemplate jdbcTemplate) {
		this.config = config;
		this.jdbcTemplate = jdbcTemplate;
		if (config.isStatistics()) {
			LOG.info("Statistics enabled");
		} else {
			LOG.warn("Statistics disabled");
		}
	}

	public void recordStartup() {
		doUpdate("startups = startups + 1");
	}

	public void recordSpoiler(int files) {
		assert files > 0;
		doUpdate("spoiler_messages = spoiler_messages + 1, spoiler_files = spoiler_files + ?", files);
	}

	public void recordXfer(int kb) {
		assert kb >= 0;
		doUpdate("xfer_kb = xfer_kb + ?", kb);
	}

	public void recordInfoProvided() {
		doUpdate("info_provided = info_provided + 1");
	}

	public void recordMessageSeen() {
		doUpdate("messages_seen = messages_seen + 1");
	}

	public void recordReactionSeen() {
		doUpdate("reactions_seen = reactions_seen + 1");
	}

	public void recordGuildJoin() {
		doUpdate("guild_joins = guild_joins + 1");
	}

	public void recordGuildLeave() {
		doUpdate("guild_leave = guild_leave + 1");
	}

	private void doUpdate(String sqlFragment, Object... args) {
		if (config.isStatistics()) {
			var sql = "update statistic set " + sqlFragment;
			LOG.trace("Executing {} {}", sql, Arrays.toString(args));
			var modifiedRows = jdbcTemplate.update(sql, args);
			assert modifiedRows == 1;
		}
	}
}
