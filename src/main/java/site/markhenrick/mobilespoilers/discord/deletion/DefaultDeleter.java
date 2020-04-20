package site.markhenrick.mobilespoilers.discord.deletion;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.markhenrick.mobilespoilers.dal.SpoilerRepository;
import site.markhenrick.mobilespoilers.dal.jooqgenerated.tables.records.Spoiler;
import site.markhenrick.mobilespoilers.discord.util.ConstantErrorRestAction;
import site.markhenrick.mobilespoilers.discord.util.ConstantRestAction;
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
		return getSpoiler(messageId)
			.flatMap(spoiler -> checkAuthority(requestingUserId, spoiler))
			.flatMap(this::getChannel)
			.flatMap(channel -> channel.deleteMessageById(messageId))
			.map(success -> {
				repo.deleteSpoiler(messageId);
				return UNIT;
			});
	}

	private RestAction<Spoiler> getSpoiler(String messageId) {
		var spoiler = repo.getSpoiler(messageId);
		return spoiler != null ? resolve(spoiler) : error(spoilerNotFound());
	}

	private RestAction<Spoiler> checkAuthority(String requestingUserId, Spoiler spoiler) {
		return spoiler.getUserId().equals(requestingUserId) ? resolve(spoiler) : error(unauthorised());
	}

	private RestAction<MessageChannel> getChannel(Spoiler spoiler) {
		var id = spoiler.getChannelId();
		var guildChannel = jda.getTextChannelById(id);
		if (guildChannel != null) return resolve(guildChannel);
		var privateChannel = jda.getPrivateChannelById(id);
		if (privateChannel != null) return resolve(privateChannel);
		return error(channelNotFound());
	}

	private <T> RestAction<T> resolve(T result) {
		return new ConstantRestAction<>(jda, result);
	}

	private <T> RestAction<T> error(RuntimeException exception) {
		return new ConstantErrorRestAction<>(jda, exception);
	}
}
