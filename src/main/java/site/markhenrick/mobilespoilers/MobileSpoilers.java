package site.markhenrick.mobilespoilers;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.GuildlistCommand;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.markhenrick.mobilespoilers.dal.SQLiteSpoilerRepository;
import site.markhenrick.mobilespoilers.discord.commands.AboutCommand;
import site.markhenrick.mobilespoilers.discord.commands.DeleteCommand;
import site.markhenrick.mobilespoilers.discord.commands.HelpCommand;
import site.markhenrick.mobilespoilers.discord.commands.SpoilCommand;
import site.markhenrick.mobilespoilers.discord.deletion.DefaultDeleter;
import site.markhenrick.mobilespoilers.discord.listeners.MetaListener;
import site.markhenrick.mobilespoilers.discord.listeners.ReactionDeletionListener;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class MobileSpoilers {
	private static final Logger LOG = LoggerFactory.getLogger(MobileSpoilers.class);

	private final Config config;
	private JDA jda;

	public MobileSpoilers(Config config) throws IllegalAccessException {
		config.validate();
		LOG.info("Constructing with config {}", config);
		this.config = config;
	}

	public boolean isRunning() {
		return jda != null;
	}

	public void start() throws LoginException {
		if (jda != null) throw new IllegalStateException("Already running");
		LOG.info("Starting");
		var repo = new SQLiteSpoilerRepository(config.getDbPath());
		var eventWaiter = new EventWaiter();
		var commandClient = new CommandClientBuilder()
			.setPrefix(config.getPrefix())
			.setOwnerId(config.getAdminUserId())
			.setHelpConsumer(new HelpCommand(config.isShowAdminInfo()))
			.addCommands(
				new GuildlistCommand(eventWaiter),
				new PingCommand(),
				new ShutdownCommand(),
				new AboutCommand(config.isShowAdminInfo()),
				new SpoilCommand(repo, config.getReaction())
			)
			.build();

		LOG.info("Connecting to Discord...");

		var jdaCandidate = new JDABuilder(config.getToken())
			.setDisabledCacheFlags(EnumSet.of(EMOTE, VOICE_STATE, ACTIVITY, CLIENT_STATUS))
			.addEventListeners(
				commandClient,
				new MetaListener()
			)
			.build();

		var deleter = new DefaultDeleter(jdaCandidate, repo);
		jdaCandidate.addEventListener(new ReactionDeletionListener(config.getReaction(), deleter));
		commandClient.addCommand(new DeleteCommand(config.getReaction(), deleter));

		jda = jdaCandidate;

		LOG.info("Startup complete");
	}

	public void stop() {
		if (jda == null) throw new IllegalStateException("Not running");
		LOG.info("Stopping...");
		jda.shutdown();
		this.jda = null;
		LOG.info("Stopped");
	}
}
