package site.markhenrick.mobilespoilers.discord.listeners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.markhenrick.mobilespoilers.discord.commands.AboutCommand;

public class MetaListener extends ListenerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(MetaListener.class);

	@Override
	public void onReady(ReadyEvent event) {
		LOG.info("Ready. Logged in as {}. {}/{} guilds available",
			event.getJDA().getSelfUser(), event.getGuildAvailableCount(), event.getGuildTotalCount());
		LOG.info("Invite link: {}", AboutCommand.getInviteLink(event.getJDA().getSelfUser().getId()));
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
