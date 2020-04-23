package site.markhenrick.mobilespoilers.discord.listeners;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;
import site.markhenrick.mobilespoilers.discord.util.BotException;
import site.markhenrick.mobilespoilers.discord.service.Deleter;

@Service
public class ReactionDeletionListener extends SelfRegisteringListener {
	private static final Logger LOG = LoggerFactory.getLogger(ReactionDeletionListener.class);

	private final MobileSpoilersConfig config;
	private final Deleter deleter;

	public ReactionDeletionListener(MobileSpoilersConfig config, Deleter deleter) {
		this.config = config;
		this.deleter = deleter;
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		var author = event.getUser();
		var emote = event.getReactionEmote();
		if (!author.isBot() && emote.isEmoji() && emote.getEmoji().contains(config.getDeletionEmoji())) {
			LOG.trace("TryDeleting message due to reaction on message {} from {} in {} of {}",
				event.getMessageId(), event.getUser(), event.getChannel(), event.getGuild());
			deleter.tryDeleteMessage(author.getIdLong(), event.getMessageIdLong()).queue(success -> {}, error -> {
				if (!(error instanceof BotException)) {
					LOG.error("Error deleting spoiler", error);
				}
			});
		}
	}
}
