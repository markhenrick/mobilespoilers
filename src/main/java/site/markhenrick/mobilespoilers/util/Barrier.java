package site.markhenrick.mobilespoilers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// TODO this is probably a reinvention of some standard library (or at least Apache) class
public class Barrier<T> {
	private final int limit;
	private final List<T> results;
	private final Consumer<? super List<T>> callback;

	public Barrier(final int limit, final Consumer<? super List<T>> callback) {
		this.limit = limit;
		this.callback = callback;
		this.results = new ArrayList<>(limit);
		if (limit == 0) {
			callback.accept(this.results);
		}
	}

	public synchronized void addResult(final T result) {
		if (results.size() == limit) throw new IllegalStateException();
		results.add(result);
		if (results.size() == limit) {
			callback.accept(results);
		}
	}
}
