package site.markhenrick.mobilespoilers.discord.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.discord.service.BotInfoService;

@Service
public class MetaListener extends SelfRegisteringListener {
	private static final Logger LOG = LoggerFactory.getLogger(MetaListener.class);

	private final BotInfoService botInfoService;

	public MetaListener(BotInfoService botInfoService) {
		this.botInfoService = botInfoService;
		LOG.info("Initialised");
	}

	@Override
	public void onReady(ReadyEvent event) {
		LOG.info("Ready. Logged in as {}. {}/{} guilds available",
			event.getJDA().getSelfUser(), event.getGuildAvailableCount(), event.getGuildTotalCount());
		LOG.info("Invite link: {}", botInfoService.getInviteLink());
	}

	@Override
	public void onShutdown(ShutdownEvent event) {
		LOG.warn("Shutting down");
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		LOG.info("Joined guild {}", event.getGuild());
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		LOG.info("Left guild {}", event.getGuild());
	}

	@Override
	public void onGuildUnavailable(GuildUnavailableEvent event) {
		LOG.info("Guild is unavailable: {}", event.getGuild());
	}

	@Override
	public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event) {
		LOG.warn("Joined unavailable guild: {}", event.getGuildId());
	}

	@Override
	public void onUnavailableGuildLeave(UnavailableGuildLeaveEvent event) {
		LOG.info("Left unavailable guild: {}", event.getGuildId());
	}
}
