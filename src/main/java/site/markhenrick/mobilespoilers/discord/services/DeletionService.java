package site.markhenrick.mobilespoilers.discord.services;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.dal.Spoiler;
import site.markhenrick.mobilespoilers.dal.SpoilerRepository;
import site.markhenrick.mobilespoilers.util.Unit;

import static site.markhenrick.mobilespoilers.util.Unit.UNIT;

@Service
public class DeletionService {
	private static final Logger LOG = LoggerFactory.getLogger(DeletionService.class);

	private final JDAHolder jdaHolder;
	private final RestActionFactory restActionFactory;
	private final SpoilerRepository repo;

	public DeletionService(JDAHolder jdaHolder, RestActionFactory restActionFactory, SpoilerRepository repo) {
		this.jdaHolder = jdaHolder;
		this.restActionFactory = restActionFactory;
		this.repo = repo;
	}

	public RestAction<Unit> tryDeleteMessage(long requestingUserId, long messageId) {
		return getSpoiler(messageId)
			.flatMap(spoiler -> checkAuthority(requestingUserId, spoiler))
			.flatMap(spoiler -> getChannel(spoiler.getChannelId()))
			.flatMap(channel -> channel.deleteMessageById(messageId))
			.map(success -> {
				LOG.debug("Deleting spoiler id {}", messageId);
				repo.deleteById(messageId);
				return UNIT;
			});
	}

	private RestAction<Spoiler> getSpoiler(long messageId) {
		return restActionFactory.fromOptional(repo.findById(messageId), "I have no record of that spoiler");
	}

	private RestAction<Spoiler> checkAuthority(long requestingUserId, Spoiler spoiler) {
		return restActionFactory.fromBoolean(spoiler.getUserId().equals(requestingUserId), spoiler, "Only the user who made that spoiler may delete it");
	}

	private RestAction<MessageChannel> getChannel(long id) {
		return restActionFactory.fromOptional(jdaHolder.getMessageChannelById(id),
			"Sorry, I no longer have access to the channel so cannot delete the spoiler");
	}
}
