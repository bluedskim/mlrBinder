package net.shed.mlrbinder;

import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.verb.Option.option;

import net.shed.mlrbinder.verb.Option;

/**
 * Shorthand {@link Option} factories for Miller {@code cat} (e.g. {@code -n}, {@code -N}).
 */
public final class CatVerbOpts {
	private CatVerbOpts() {
	}

	/** {@code cat -n} — prepend record counter field {@code n}. */
	public static Option recordCounter() {
		return option(SortFlags.n());
	}

	/** {@code cat -N} {@code name} — prepend counter under a custom field name. */
	public static Option recordCounterField(String name) {
		return option(Flag.flag("-N"), objective(name));
	}
}
