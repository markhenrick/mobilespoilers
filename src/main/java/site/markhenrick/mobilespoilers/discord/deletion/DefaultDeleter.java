package site.markhenrick.mobilespoilers.discord.deletion;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.markhenrick.mobilespoilers.dal.SpoilerRepository;
import site.markhenrick.mobilespoilers.discord.util.ConstantErrorRestAction;
import site.markhenrick.mobilespoilers.util.Unit;

import static site.markhenrick.mobilespoilers.discord.deletion.DeletionException.*;
import static site.markhenrick.mobilespoilers.util.Unit.UNIT;

public class DefaultDeleter implements Deleter {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultDeleter.class);

	private final JDA jda;
	private final SpoilerRepository repo;

	public DefaultDeleter(JDA jda, SpoilerRepository repo) {
		LOG.info("Initialised deleter");
		this.jda = jda;
		this.repo = repo;
		LOG.trace("Initialised");
	}

	@Override
	public RestAction<Unit> tryDeleteMessage(String requestingUserId, String messageId) {
		try {
			var spoiler = repo.getSpoiler(messageId);
			if (spoiler == null) return error(spoilerNotFound());
			if (!spoiler.getUserId().equals(requestingUserId)) return error(unauthorised());
			MessageChannel channel = jda.getTextChannelById(spoiler.getChannelId());
			if (channel == null) channel = jda.getPrivateChannelById(spoiler.getChannelId());
			if (channel == null) return error(channelNotFound());
			return channel.deleteMessageById(messageId)
				.map(success -> {
					repo.deleteSpoiler(spoiler);
					return UNIT;
				});
		} catch (RuntimeException e) {
			LOG.error("Error", e);
			return error(e);
		}
	}

	private RestAction<Unit> error(RuntimeException exception) {
		return new ConstantErrorRestAction<>(jda, exception);
	}
}
