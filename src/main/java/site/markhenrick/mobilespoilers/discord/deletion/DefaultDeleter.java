package site.markhenrick.mobilespoilers.discord.deletion;

import net.dv8tion.jda.api.entities.MessageChannel;
import site.markhenrick.mobilespoilers.dal.SpoilerRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.markhenrick.mobilespoilers.discord.util.ConstantRestAction;

import static site.markhenrick.mobilespoilers.discord.deletion.Deleter.DeletionResult.*;

public class DefaultDeleter implements Deleter {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultDeleter.class);

	private final JDA jda;
	private final SpoilerRepository repo;

	public DefaultDeleter(final JDA jda, final SpoilerRepository repo) {
		LOG.info("Initialised deleter");
		this.jda = jda;
		this.repo = repo;
		LOG.trace("Initialised");
	}

	@Override
	public RestAction<DeletionResult> tryDeleteMessage(final String requestingUserId, final String messageId) {
		try {
			final var spoiler = repo.getSpoiler(messageId);
			if (spoiler == null) return resolve(SPOILER_NOT_FOUND);
			if (!spoiler.getUserId().equals(requestingUserId)) return resolve(UNAUTHORISED);
			MessageChannel channel = jda.getTextChannelById(spoiler.getChannelId());
			if (channel == null) channel = jda.getPrivateChannelById(spoiler.getChannelId());
			if (channel == null) return resolve(CHANNEL_NOT_FOUND);
			return channel.deleteMessageById(messageId)
				.map((success) -> {
					repo.deleteSpoiler(spoiler);
					return SUCCESS;
				})
				.onErrorMap((error) -> {
					LOG.error("Error deleting spoiler", error);
					return UNKNOWN_ERROR;
				});
		} catch (final RuntimeException e) {
			LOG.error("Error", e);
			return resolve(UNKNOWN_ERROR);
		}
	}

	private RestAction<DeletionResult> resolve(final DeletionResult result) {
		return new ConstantRestAction<>(jda, result);
	}
}
