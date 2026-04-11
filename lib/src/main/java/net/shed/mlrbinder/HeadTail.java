package net.shed.mlrbinder;

import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.verb.Option.option;

import net.shed.mlrbinder.verb.Option;

/**
 * Shorthand for Miller {@code head} / {@code tail} count option {@code -n &lt;count&gt;}.
 * <p>
 * For {@code sort -n field} use {@link SortFlags#n(String)} instead of {@link #n(int)}.
 * </p>
 */
public final class HeadTail {
	private HeadTail() {
	}

	/** {@code -n} then record count (e.g. {@code head -n 4}). */
	public static Option n(int count) {
		return option(SortFlags.n(), objective(Integer.toString(count)));
	}
}
