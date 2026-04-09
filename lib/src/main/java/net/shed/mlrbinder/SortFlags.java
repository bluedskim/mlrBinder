package net.shed.mlrbinder;

/**
 * Shorthand {@link Flag} factories for Miller {@code sort} (e.g. {@code -n}, {@code -nr}).
 * <p>
 * Typical use with static import: {@code import static net.shed.mlrbinder.SortFlags.*;}
 * then {@code .sort(n("a"), nr("b"))}.
 * </p>
 */
public final class SortFlags {
	private SortFlags() {
	}

	/** Numeric ascending sort key (Miller {@code -n}). */
	public static Flag n(String field) {
		return new Flag("-n").objective(field);
	}

	/** Numeric descending sort key (Miller {@code -nr}). */
	public static Flag nr(String field) {
		return new Flag("-nr").objective(field);
	}
}
