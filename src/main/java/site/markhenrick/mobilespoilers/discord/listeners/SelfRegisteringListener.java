package site.markhenrick.mobilespoilers.discord.listeners;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import site.markhenrick.mobilespoilers.discord.services.JDAHolder;

import javax.annotation.PostConstruct;

abstract class SelfRegisteringListener extends ListenerAdapter {
	@Autowired
	private JDAHolder jdaHolder;

	@PostConstruct
	public void register() {
		jdaHolder.addEventListener(this);
	}
}
