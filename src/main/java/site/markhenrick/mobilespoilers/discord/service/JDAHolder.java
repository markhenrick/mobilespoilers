package site.markhenrick.mobilespoilers.discord.service;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.GuildlistCommand;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;
import site.markhenrick.mobilespoilers.discord.commands.HelpCommand;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JDAHolder {
	private static final Logger LOG = LoggerFactory.getLogger(JDAHolder.class);

	private final MobileSpoilersConfig config;
	private final HelpCommand helpCommand;

	private List<Object> eventListenerQueue;
	private List<Command> commandQueue;

	@Getter
	private JDA jda;

	@Getter
	private CommandClient commandClient;

	public JDAHolder(MobileSpoilersConfig config, HelpCommand helpCommand) {
		this.config = config;
		this.helpCommand = helpCommand;
		this.eventListenerQueue = new ArrayList<>();
		this.commandQueue = new ArrayList<>();
	}

	public void addEventListener(Object listener) {
		if (jda != null) {
			LOG.debug("Adding listener {} directly", listener.getClass());
			jda.addEventListener(listener);
		} else {
			LOG.debug("Queueing listener {} to add later", listener.getClass());
			eventListenerQueue.add(listener);
		}
	}

	public void addCommand(Command command) {
		if (jda != null) {
			LOG.debug("Adding command {} directly", command.getClass());
			commandClient.addCommand(command);
		} else {
			LOG.debug("Queueing command {} to add later", command.getClass());
			commandQueue.add(command);
		}
	}

	public void start() throws LoginException {
		LOG.info("Starting JDA");
		if (jda != null) throw new IllegalStateException("Already running");

		this.commandClient = new CommandClientBuilder()
			.setPrefix(config.getPrefix())
			.setOwnerId(config.getAdminUserId())
			.setHelpConsumer(helpCommand)
			.addCommands(new GuildlistCommand(new EventWaiter()), new ShutdownCommand())
			.addCommands(commandQueue.toArray(new Command[0]))
			.build();

		this.jda = new JDABuilder(config.getToken())
			.addEventListeners(commandClient)
			.addEventListeners(eventListenerQueue.toArray())
			.build();

		eventListenerQueue = null;
		commandQueue = null;
		LOG.info("Started JDA");
	}

	public Optional<MessageChannel> getMessageChannelById(long id) {
		return Optional.ofNullable((MessageChannel) jda.getTextChannelById(id))
			.or(() -> Optional.ofNullable(jda.getPrivateChannelById(id)));
	}
}
