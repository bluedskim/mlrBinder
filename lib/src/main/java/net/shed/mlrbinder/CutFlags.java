package net.shed.mlrbinder;

/**
 * Shorthand {@link Flag} factories for Miller {@code cut} (e.g. {@code -f}, {@code -o}, {@code -x}).
 * <p>
 * Typical use: {@code cut(option(CutFlags.o()), option(CutFlags.f("a,b")))} or {@link Mlr#cutOrdered(String)}.
 * </p>
 */
public final class CutFlags {
	private CutFlags() {
	}

	/** Miller {@code cut -o} (reorder fields to match {@code -f} list). */
	public static Flag o() {
		return Flag.flag("-o");
	}

	/** Miller {@code cut -x} (omit listed fields). */
	public static Flag x() {
		return Flag.flag("-x");
	}

	/** Miller {@code cut -f} with a comma-separated field list. */
	public static Flag f(String fields) {
		return Flag.flag("-f").objective(fields);
	}
}
