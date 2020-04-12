package site.markhenrick.mobilespoilers.dal;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;
import site.markhenrick.mobilespoilers.dal.jooqgenerated.tables.records.Spoiler;

import static site.markhenrick.mobilespoilers.dal.jooqgenerated.Tables.SPOILERS;

public class SQLiteSpoilerRepository implements SpoilerRepository {
	private static final Logger LOG = LoggerFactory.getLogger(SQLiteSpoilerRepository.class);

	private final DSLContext create;

	public SQLiteSpoilerRepository(final String dbPath) {
		LOG.info("Constructing with { dbPath = {} }", dbPath);
		final var ds = new SQLiteDataSource();
		ds.setUrl(String.format("jdbc:sqlite:%s", dbPath));
		Flyway.configure().dataSource(ds).load().migrate();
		this.create = DSL.using(ds, SQLDialect.SQLITE);
		LOG.trace("Constructed");
	}

	@Override
	public void recordSpoiler(final String messageId, final String channelId, final String userId) {
		LOG.trace("Recording spoiler { messageId: {}, channelId: {}, userId: {} }", messageId, channelId, userId);
		final var newSpoiler = create.newRecord(SPOILERS);
		newSpoiler.setMessageId(messageId);
		newSpoiler.setChannelId(channelId);
		newSpoiler.setUserId(userId);
		newSpoiler.store();
	}

	@Override
	@Nullable
	public Spoiler getSpoiler(final String messageId) {
		return create.fetchOne(SPOILERS, SPOILERS.MESSAGE_ID.eq(messageId));
	}

	@Override
	public void deleteSpoiler(final Spoiler spoiler) {
		LOG.trace("Deleting spoiler {}", spoiler);
		try {
			spoiler.delete();
		} catch (final RuntimeException e) {
			LOG.error("Error while deleting spoiler", e);
		}
	}
}
