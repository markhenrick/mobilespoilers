package site.markhenrick.mobilespoilers;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.GuildlistCommand;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public final class EntryPoint {
	private static final Logger LOG = LoggerFactory.getLogger(EntryPoint.class);

	private EntryPoint() { }

	public static void main(String... args) throws LoginException, IOException, IllegalAccessException {
		LOG.info("Starting with args {}", Arrays.toString(args));

		if (args.length > 1) {
			System.err.println("Usage: mobilespoilers [config yaml path]");
			System.exit(1);
		}
		var configPath = args.length == 0 ? "config.yaml" : args[0];

		var config = Config.loadFromYaml(configPath);
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

		var jda = new JDABuilder(config.getToken())
			.setDisabledCacheFlags(EnumSet.of(EMOTE, VOICE_STATE, ACTIVITY, CLIENT_STATUS))
			.addEventListeners(
				commandClient,
				new MetaListener()
			)
			.build();

		var deleter = new DefaultDeleter(jda, repo);
		jda.addEventListener(new ReactionDeletionListener(config.getReaction(), deleter));
		commandClient.addCommand(new DeleteCommand(config.getReaction(), deleter));

		LOG.info("Startup complete");
	}
}
