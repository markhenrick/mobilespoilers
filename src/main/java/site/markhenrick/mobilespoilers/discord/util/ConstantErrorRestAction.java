package site.markhenrick.mobilespoilers.discord.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ConstantErrorRestAction<T> implements AuditableRestAction<T> {
	private final JDA jda;
	private final RuntimeException error;

	public ConstantErrorRestAction(JDA jda, RuntimeException error) {
		this.jda = jda;
		this.error = error;
	}

	@Override
	public JDA getJDA() {
		return jda;
	}

	@Override
	public AuditableRestAction<T> setCheck(BooleanSupplier checks) {
		return this;
	}

	@Override
	public void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
		if (failure != null) {
			failure.accept(error);
		}
	}

	@Override
	public T complete(boolean shouldQueue) {
		throw error;
	}

	@Override
	public CompletableFuture<T> submit(boolean shouldQueue) {
		var future = new CompletableFuture<T>();
		future.completeExceptionally(error);
		return future;
	}

	@Override
	public AuditableRestAction<T> reason(String reason) {
		return this;
	}

	@Override
	public AuditableRestAction<T> timeout(long timeout, TimeUnit unit) {
		return this;
	}

	@Override
	public AuditableRestAction<T> deadline(long timestamp) {
		return this;
	}
}
