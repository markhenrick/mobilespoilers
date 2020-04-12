package site.markhenrick.mobilespoilers;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.GuildlistCommand;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.markhenrick.mobilespoilers.config.Config;
import site.markhenrick.mobilespoilers.dal.SQLiteSpoilerRepository;
import site.markhenrick.mobilespoilers.discord.commands.AboutCommand;
import site.markhenrick.mobilespoilers.discord.commands.DeleteCommand;
import site.markhenrick.mobilespoilers.discord.commands.HelpCommand;
import site.markhenrick.mobilespoilers.discord.commands.SpoilCommand;
import site.markhenrick.mobilespoilers.discord.deletion.DefaultDeleter;
import site.markhenrick.mobilespoilers.discord.listeners.MetaListener;
import site.markhenrick.mobilespoilers.discord.listeners.ReactionDeletionListener;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

public final class EntryPoint {
	private static final Logger LOG = LoggerFactory.getLogger(EntryPoint.class);

	private EntryPoint() { }

	public static void main(final String... args) throws LoginException, IOException {
		LOG.info("Starting with args {}", Arrays.toString(args));

		if (args.length > 1) {
			System.err.println("Usage: mobilespoilers [config yaml path]");
			System.exit(1);
		}
		final var configPath = args.length == 0 ? "config.yaml" : args[0];

		final var config = Config.loadFromYaml(configPath);
		final var repo = new SQLiteSpoilerRepository(config.getDbPath());
		final var eventWaiter = new EventWaiter();
		final var commandClient = new CommandClientBuilder()
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

		final var jda = new JDABuilder(config.getToken())
			.setDisabledCacheFlags(EnumSet.of(CacheFlag.EMOTE, CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS))
			.addEventListeners(
				commandClient,
				new MetaListener()
			)
			.build();

		final var deleter = new DefaultDeleter(jda, repo);
		jda.addEventListener(new ReactionDeletionListener(config.getReaction(), deleter));
		commandClient.addCommand(new DeleteCommand(config.getReaction(), deleter));

		LOG.info("Startup complete");
	}
}
