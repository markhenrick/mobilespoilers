package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import site.markhenrick.mobilespoilers.discord.JDAHolder;

import javax.annotation.PostConstruct;

public abstract class SelfRegisteringCommand extends Command {
	@Autowired
	private JDAHolder jdaFacade;

	@PostConstruct
	public void register() {
		jdaFacade.addCommand(this);
	}
}
