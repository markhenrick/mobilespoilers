package site.markhenrick.mobilespoilers.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;

import java.util.regex.Pattern;

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

	public boolean isEnabled() {
		return config.isStatistics();
	}

	public void recordStartup() {
		doIncrement(Column.STARTUPS);
	}

	public long getStartups() {
		return getStat(Column.STARTUPS);
	}

	public void recordSpoiler(int files) {
		if (files <= 0) throw new IllegalArgumentException("files must be strictly positive");
		doIncrement(Column.SPOILER_MESSAGES);
		doIncrement(Column.SPOILER_FILES, files);
	}

	public long getSpoilerMessages() {
		return getStat(Column.SPOILER_MESSAGES);
	}

	public long getSpoilerFiles() {
		return getStat(Column.SPOILER_FILES);
	}

	public void recordXfer(int byteCount) {
		if (byteCount < 0) throw new IllegalArgumentException("byteCount must be positive");
		doIncrement(Column.XFER_KB, byteCount / 1024);
	}

	public String getSpoilerXfer() {
		return prettyPrintSize(getStat(Column.SPOILER_MESSAGES));
	}

	public void recordInfoProvided() {
		doIncrement(Column.INFO_PROVIDED);
	}

	public long getInfosProvided() {
		return getStat(Column.INFO_PROVIDED);
	}

	public void recordMessageSeen() {
		doIncrement(Column.MESSAGES_SEEN);
	}

	public long getMessagesSeen() {
		return getStat(Column.MESSAGES_SEEN);
	}

	public void recordReactionSeen() {
		doIncrement(Column.REACTIONS_SEEN);
	}

	public long getReactionsSeen() {
		return getStat(Column.REACTIONS_SEEN);
	}

	public void recordGuildJoin() {
		doIncrement(Column.GUILD_JOINS);
	}

	public long getGuildJoins() {
		return getStat(Column.GUILD_JOINS);
	}

	public void recordGuildLeave() {
		doIncrement(Column.GUILD_LEAVES);
	}

	public long getGuildLeaves() {
		return getStat(Column.GUILD_LEAVES);
	}

	private void doIncrement(Column column) {
		doIncrement(column, 1);
	}

	private void doIncrement(Column column, int increase) {
		if (isEnabled()) {
			var sql = "update statistic set " + column + " = " + column + " + ?";
			LOG.trace("Executing {} {}", sql, increase);
			var modifiedRows = jdbcTemplate.update(sql, increase);
			assert modifiedRows == 1;
		}
	}

	private Long getStat(Column column) {
		if (!isEnabled()) throw new IllegalStateException("Statistics disabled");
		return jdbcTemplate.queryForObject("select " + column + " from statistic", Long.class);
	}

	static String prettyPrintSize(long kiloByteCount) {
		if (kiloByteCount < 0) throw new IllegalArgumentException("kiloByteCount must be positive");
		if (kiloByteCount == 0) return "0KiB";
		final var order = (int) (Math.log(kiloByteCount) / Math.log(1024));
		@SuppressWarnings("SpellCheckingInspection") final var orderName = "KMGTPEZ".charAt(order);
		final var mantissa = (int) (kiloByteCount / Math.pow(1024, order));
		return String.format("%s%siB", mantissa, orderName);
	}

	enum Column {
		STARTUPS, SPOILER_MESSAGES, SPOILER_FILES, XFER_KB, INFO_PROVIDED, MESSAGES_SEEN, REACTIONS_SEEN, GUILD_JOINS, GUILD_LEAVES;

		private static final Pattern SAFE_CHARS = Pattern.compile("^[a-zA-Z_]+$");

		// This is NOT rigorous protection against SQL injection, just a basic sanity check
		static boolean isSafeColumnName(CharSequence name) {
			return SAFE_CHARS.matcher(name).matches();
		}

		@Override
		public String toString() {
			var name = super.name();
			assert isSafeColumnName(name);
			return name;
		}
	}
}
