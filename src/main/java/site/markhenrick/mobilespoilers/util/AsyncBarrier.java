package site.markhenrick.mobilespoilers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Accept a defined number of results and then run a callback
 */
public class AsyncBarrier<T> {
	private final int limit;
	private final List<T> results;
	private final Consumer<? super List<T>> callback;

	/**
	 * Accept `limit` results and then run `callback`
	 * @param limit The number of results to expect. If this is 0, `callback` is run immediately on an empty list
	 * @param callback The consumer to run when the barrier is full. The supplied list is never null
	 */
	public AsyncBarrier(final int limit, final Consumer<? super List<T>> callback) {
		this.limit = limit;
		this.callback = callback;
		this.results = new ArrayList<>(limit);
		if (limit == 0) {
			callback.accept(this.results);
		}
	}

	/**
	 * Add a result. If the barrier becomes full, the callback is run (in the same thread as the call).
	 * Otherwise, control returns immediately
	 * @param result The result to add
	 * @throws IllegalStateException If the barrier is already full
	 */
	public synchronized void addResult(final T result) {
		if (results.size() == limit) throw new IllegalStateException();
		results.add(result);
		if (results.size() == limit) {
			callback.accept(results);
		}
	}
}
