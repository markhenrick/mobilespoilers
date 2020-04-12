package site.markhenrick.mobilespoilers.discord.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

// TODO this is probably a reinvention of some standard library (or at least Apache) class
public class Barrier<T> {
	private final int limit;
	private final Collection<T> results;
	private final Consumer<? super Collection<T>> callback;

	public Barrier(final int limit, final Consumer<? super Collection<T>> callback) {
		this.limit = limit;
		this.results = new ArrayList<>(limit);
		this.callback = callback;
	}

	public synchronized void addResult(final T result) {
		if (results.size() == limit) throw new IllegalStateException();
		results.add(result);
		if (results.size() == limit) {
			callback.accept(results);
		}
	}
}
