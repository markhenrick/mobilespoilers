package site.markhenrick.mobilespoilers.discord.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.discord.services.BotInfoService;
import site.markhenrick.mobilespoilers.services.StatisticsService;

import javax.annotation.Nonnull;

@Service
public class MetaListener extends SelfRegisteringListener {
	private static final Logger LOG = LoggerFactory.getLogger(MetaListener.class);

	private final BotInfoService botInfoService;
	private final StatisticsService stats;

	public MetaListener(BotInfoService botInfoService, StatisticsService stats) {
		this.botInfoService = botInfoService;
		this.stats = stats;
	}

	@Override
	public void onReady(ReadyEvent event) {
		LOG.info("Ready. Logged in as {}. {}/{} guilds available",
			event.getJDA().getSelfUser(), event.getGuildAvailableCount(), event.getGuildTotalCount());
		LOG.info("Invite link: {}", botInfoService.getInviteLink());
		stats.recordStartup();
	}

	@Override
	public void onShutdown(ShutdownEvent event) {
		LOG.warn("Shutting down");
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event) {
		LOG.info("Joined guild {}", event.getGuild());
		stats.recordGuildJoin();
	}

	@Override
	public void onGuildLeave(GuildLeaveEvent event) {
		LOG.info("Left guild {}", event.getGuild());
		stats.recordGuildLeave();
	}

	@Override
	public void onGuildUnavailable(GuildUnavailableEvent event) {
		LOG.info("Guild is unavailable: {}", event.getGuild());
	}

	@Override
	public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event) {
		LOG.warn("Joined unavailable guild: {}", event.getGuildId());
		stats.recordGuildJoin();
	}

	@Override
	public void onUnavailableGuildLeave(UnavailableGuildLeaveEvent event) {
		LOG.info("Left unavailable guild: {}", event.getGuildId());
		stats.recordGuildLeave();
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		stats.recordMessageSeen();
	}

	@Override
	public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
		stats.recordReactionSeen();
	}
}
