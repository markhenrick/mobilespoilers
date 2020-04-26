package site.markhenrick.mobilespoilers.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;

import java.time.LocalDateTime;

@SuppressWarnings("OverlyBroadCatchBlock")
@Service
public class StatisticsService {
	// Contract: since this is a non-essential service, non-constructor public methods should never throw exceptions
	// as far as reasonably possible. Instead just LOG.error, no-op, and return null

	private static final Logger LOG = LoggerFactory.getLogger(StatisticsService.class);

	private final MobileSpoilersConfig config;
	private final JdbcTemplate jdbcTemplate;

	public StatisticsService(MobileSpoilersConfig config, JdbcTemplate jdbcTemplate) {
		this.config = config;
		this.jdbcTemplate = jdbcTemplate;
		ensureRow();
		LOG.info("Epoch {}", getEpoch());
		if (config.isStatistics()) {
			LOG.info("Statistics enabled");
		} else {
			LOG.warn("Statistics disabled");
		}
	}

	public LocalDateTime getEpoch() {
		return jdbcTemplate.queryForObject("select epoch from statistic", LocalDateTime.class);
	}

	public boolean isEnabled() {
		return config.isStatistics();
	}

	public void recordStartup() {
		doIncrement(LongCompatibleColumn.STARTUPS);
	}

	public Long getStartups() {
		return getStat(LongCompatibleColumn.STARTUPS);
	}

	public void recordSpoiler(int files) {
		if (files > 0) {
			doIncrement(LongCompatibleColumn.SPOILER_MESSAGES);
			doIncrement(LongCompatibleColumn.SPOILER_FILES, files);
		} else {
			LOG.error("Illegal argument for recordSpoiler: files={}", files);
		}
	}

	public Long getSpoilerMessages() {
		return getStat(LongCompatibleColumn.SPOILER_MESSAGES);
	}

	public Long getSpoilerFiles() {
		return getStat(LongCompatibleColumn.SPOILER_FILES);
	}

	public void recordXfer(int byteCount) {
		if (byteCount >= 0) {
			doIncrement(LongCompatibleColumn.XFER_KB, byteCount / 1024);
		} else {
			LOG.error("Illegal argument for recordXfer: byteCount={}", byteCount);
		}
	}

	public String getSpoilerXfer() {
		return prettyPrintSize(getStat(LongCompatibleColumn.SPOILER_MESSAGES));
	}

	public void recordInfoProvided() {
		doIncrement(LongCompatibleColumn.INFO_PROVIDED);
	}

	public Long getInfosProvided() {
		return getStat(LongCompatibleColumn.INFO_PROVIDED);
	}

	public void recordMessageSeen() {
		doIncrement(LongCompatibleColumn.MESSAGES_SEEN);
	}

	public Long getMessagesSeen() {
		return getStat(LongCompatibleColumn.MESSAGES_SEEN);
	}

	public void recordReactionSeen() {
		doIncrement(LongCompatibleColumn.REACTIONS_SEEN);
	}

	public Long getReactionsSeen() {
		return getStat(LongCompatibleColumn.REACTIONS_SEEN);
	}

	public void recordGuildJoin() {
		doIncrement(LongCompatibleColumn.GUILD_JOINS);
	}

	public Long getGuildJoins() {
		return getStat(LongCompatibleColumn.GUILD_JOINS);
	}

	public void recordGuildLeave() {
		doIncrement(LongCompatibleColumn.GUILD_LEAVES);
	}

	public Long getGuildLeaves() {
		return getStat(LongCompatibleColumn.GUILD_LEAVES);
	}

	private void ensureRow() {
		if (isEnabled()) {
			var modifiedRows =jdbcTemplate.update(
				"insert into statistic(epoch) values (?) on conflict do nothing", LocalDateTime.now());
			assert modifiedRows < 2;
			if (modifiedRows == 1) {
				LOG.info("Recorded epoch");
			}
		}
	}

	private void doIncrement(LongCompatibleColumn column) {
		doIncrement(column, 1);
	}

	private void doIncrement(LongCompatibleColumn column, int increase) {
		try {
			if (isEnabled()) {
				var sql = "update statistic set " + column.name() + " = " + column.name() + " + ?";
				LOG.trace("Updating \"{}\" arg={}", sql, increase);
				var modifiedRows = jdbcTemplate.update(sql, increase);
				assert modifiedRows == 1;
			}
		} catch (Exception e) {
			LOG.error("Exception whilst updating", e);
		}
	}

	private Long getStat(LongCompatibleColumn column) {
		try {
			if (isEnabled()) {
				var sql = "select " + column + " from statistic";
				LOG.trace("Querying \"{}\"", sql);
				var value = jdbcTemplate.queryForObject("select " + column.name() + " from statistic", Long.class);
				if (value == null) LOG.error("Value returned for {} was null", column);
				return value;
			} else {
				LOG.error("Attempt to read column {} when stats are disabled", column);
				return null;
			}
		} catch (Exception e) {
			LOG.error("Exception whilst querying", e);
			return null;
		}
	}

	static String prettyPrintSize(Long kiloByteCount) {
		try {
			if (kiloByteCount < 0) throw new IllegalArgumentException("kiloByteCount must be positive");
			if (kiloByteCount == 0) return "0KiB";
			final var order = (int) (Math.log(kiloByteCount) / Math.log(1024));
			@SuppressWarnings("SpellCheckingInspection") final var orderName = "KMGTPEZ".charAt(order);
			final var mantissa = (int) (kiloByteCount / Math.pow(1024, order));
			return String.format("%s%siB", mantissa, orderName);
		} catch (Exception e) {
			LOG.error("Error pretty printing", e);
			return null;
		}
	}

	enum LongCompatibleColumn {
		STARTUPS, SPOILER_MESSAGES, SPOILER_FILES, XFER_KB, INFO_PROVIDED, MESSAGES_SEEN, REACTIONS_SEEN, GUILD_JOINS, GUILD_LEAVES;
	}
}
