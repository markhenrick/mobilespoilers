package site.markhenrick.mobilespoilers.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@SuppressWarnings("SqlWithoutWhere")
@Service
public class StatisticsService {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsService.class);

	private final JdbcTemplate jdbcTemplate;

	public StatisticsService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void recordStartup() {
		LOG.trace("Recording startup");
		doUpdate("startups = startups + 1");
	}

	public void recordSpoiler(int files) {
		LOG.trace("Recording spoiler of {} files", files);
		assert files > 0;
		doUpdate("spoiler_messages = spoiler_messages + 1, spoiler_files = spoiler_files + ?", files);
	}

	public void recordXfer(int kb) {
		LOG.trace("Recording {}kb xfer", kb);
		assert kb >= 0;
		doUpdate("xfer_kb = xfer_kb + ?", kb);
	}

	public void recordInfoProvided() {
		LOG.trace("Recording info provided");
		doUpdate("info_provided = info_provided + 1");
	}

	public void recordMessageSeen() {
		LOG.trace("Recording message seen");
		doUpdate("messages_seen = messages_seen + 1");
	}

	public void recordReactionSeen() {
		LOG.trace("Recording reaction seen");
		doUpdate("reactions_seen = reactions_seen + 1");
	}

	public void recordGuildJoin() {
		LOG.trace("Recording guild join");
		doUpdate("guild_joins = guild_joins + 1");
	}

	public void recordGuildLeave() {
		LOG.trace("Recording guild leave");
		doUpdate("guild_leave = guild_leave + 1");
	}

	private void doUpdate(String sqlFragment, Object... args) {
		var sql = "update statistic set " + sqlFragment;
		LOG.trace("Executing {} {}", sql, Arrays.toString(args));
		var modifiedRows = jdbcTemplate.update(sql, args);
		assert modifiedRows == 1;
	}
}
