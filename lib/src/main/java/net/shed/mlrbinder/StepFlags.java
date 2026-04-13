package net.shed.mlrbinder;

import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.verb.Option.option;

import net.shed.mlrbinder.verb.Option;

/**
 * Shorthand {@link Option} pairs for Miller {@code step}.
 */
public final class StepFlags {
	private StepFlags() {
	}

	/** {@code step -a delta -f field}. */
	public static Option[] deltaOn(String field) {
		return new Option[] {
				option(Flag.flag("-a"), objective("delta")),
				option(Flag.flag("-f"), objective(field)),
		};
	}
}
