package site.markhenrick.mobilespoilers.discord.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaListener extends ListenerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(MetaListener.class);

	@Override
	public void onReady(final ReadyEvent event) {
		LOG.info("Ready. Logged in as {}. {}/{} guilds available",
			event.getJDA().getSelfUser(), event.getGuildAvailableCount(), event.getGuildTotalCount());
	}

	@Override
	public void onShutdown(final ShutdownEvent event) {
		LOG.warn("Shutting down");
	}

	@Override
	public void onGuildJoin(final GuildJoinEvent event) {
		LOG.info("Joined guild {}", event.getGuild());
	}

	@Override
	public void onGuildLeave(final GuildLeaveEvent event) {
		LOG.info("Left guild {}", event.getGuild());
	}

	@Override
	public void onGuildUnavailable(final GuildUnavailableEvent event) {
		LOG.info("Guild is unavailable: {}", event.getGuild());
	}

	@Override
	public void onUnavailableGuildJoined(final UnavailableGuildJoinedEvent event) {
		LOG.warn("Joined unavailable guild: {}", event.getGuildId());
	}

	@Override
	public void onUnavailableGuildLeave(final UnavailableGuildLeaveEvent event) {
		LOG.info("Left unavailable guild: {}", event.getGuildId());
	}
}
