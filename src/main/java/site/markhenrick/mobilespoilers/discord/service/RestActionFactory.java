package site.markhenrick.mobilespoilers.discord.service;

import net.dv8tion.jda.api.requests.RestAction;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.discord.util.BotException;
import site.markhenrick.mobilespoilers.discord.util.ConstantErrorRestAction;
import site.markhenrick.mobilespoilers.discord.util.ConstantRestAction;

import java.util.Optional;

@Service
public class RestActionFactory {
	private final JDAHolder jdaHolder;

	public RestActionFactory(JDAHolder jdaHolder) {
		this.jdaHolder = jdaHolder;
	}

	public <T> RestAction<T> resolve(T result) {
		return new ConstantRestAction<>(jdaHolder.getJda(), result);
	}

	public <T> RestAction<T> error(String message) {
		return new ConstantErrorRestAction<>(jdaHolder.getJda(), new BotException(message));
	}

	public <T> RestAction<T> fromOptional(Optional<T> result, String errorMessage) {
		return result.map(this::resolve).orElseGet(() -> error(errorMessage));
	}

	public <T> RestAction<T> fromBoolean(boolean predicate, T result, String errorMessage) {
		return predicate ? resolve(result) : error(errorMessage);
	}
}
