package site.markhenrick.mobilespoilers.discord.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ConstantRestAction<T> implements RestAction<T> {
	private final JDA jda;
	private final T result;

	public ConstantRestAction(final JDA jda, final T result) {
		this.jda = jda;
		this.result = result;
	}

	@Override
	public JDA getJDA() {
		return jda;
	}

	@Override
	public RestAction<T> setCheck(final BooleanSupplier checks) {
		return this;
	}

	@Override
	public void queue(final Consumer<? super T> success, final Consumer<? super Throwable> failure) {
		if (success != null) success.accept(result);
	}

	@Override
	public T complete(final boolean shouldQueue) {
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompletableFuture<T> submit(final boolean shouldQueue) {
		try {
			final var privateConstructor = CompletableFuture.class.getDeclaredConstructor(Object.class);
			privateConstructor.setAccessible(true); // Is there a dab emoji yet?
			return privateConstructor.newInstance(result);
		} catch (final NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
