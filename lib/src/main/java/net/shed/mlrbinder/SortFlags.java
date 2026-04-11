package net.shed.mlrbinder;

/**
 * Shorthand {@link Flag} factories for Miller {@code sort} (e.g. {@code -n}, {@code -nr}).
 * <p>
 * Typical use with static import: {@code import static net.shed.mlrbinder.SortFlags.*;}
 * then {@code .sort(n("a"), nr("b"))}.
 * </p>
 * <p>
 * For verbs like {@code head} / {@code tail}, {@code -n} takes the count as the next argv token.
 * Use {@link #n()} with a separate value (e.g. {@code option(n(), objective("4"))}),
 * not {@link #n(String)} (that is for {@code sort -n field}).
 * </p>
 */
public final class SortFlags {
	private SortFlags() {
	}

	/**
	 * Miller {@code -n} without a bundled value — the next token is the count (e.g. {@code head -n 4}).
	 * For {@code sort -n fieldname} use {@link #n(String)} instead.
	 */
	public static Flag n() {
		return Flag.flag("-n");
	}

	/** Numeric ascending sort key (Miller {@code -n}). */
	public static Flag n(String field) {
		return Flag.flag("-n").objective(field);
	}

	/** Numeric descending sort key (Miller {@code -nr}). */
	public static Flag nr(String field) {
		return Flag.flag("-nr").objective(field);
	}

	/** Lexical ascending sort key (Miller {@code -f}). */
	public static Flag f(String field) {
		return Flag.flag("-f").objective(field);
	}
}
