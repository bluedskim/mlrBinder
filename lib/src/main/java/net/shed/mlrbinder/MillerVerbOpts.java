package net.shed.mlrbinder;

import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.verb.Option.option;

import net.shed.mlrbinder.verb.Option;

/**
 * Verb options shared by several Miller commands (e.g. {@code head -g}, {@code split -g}).
 */
public final class MillerVerbOpts {
	private MillerVerbOpts() {
	}

	/** Miller {@code -g} with a single field name (group-by). */
	public static Option groupBy(String field) {
		return option(Flag.flag("-g"), objective(field));
	}
}
