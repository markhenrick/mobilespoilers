package site.markhenrick.mobilespoilers.discord.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ConstantRestAction<T> implements RestAction<T> {
	private final JDA jda;
	private final T result;

	ConstantRestAction(JDA jda, T result) {
		this.jda = jda;
		this.result = result;
	}

	@Override
	public JDA getJDA() {
		return jda;
	}

	@Override
	public RestAction<T> setCheck(BooleanSupplier checks) {
		return this;
	}

	@Override
	public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
		if (success != null) success.accept(result);
	}

	@Override
	public T complete(boolean shouldQueue) {
		return result;
	}

	@Override
	public CompletableFuture<T> submit(boolean shouldQueue) {
		var future = new CompletableFuture<T>();
		future.complete(result);
		return future;
	}
}
