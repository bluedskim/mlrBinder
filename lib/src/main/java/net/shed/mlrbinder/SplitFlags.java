package net.shed.mlrbinder;

/**
 * Shorthand for Miller {@code split} options.
 */
public final class SplitFlags {
	private SplitFlags() {
	}

	/** Miller {@code split -g field} (emit separate files per distinct {@code field}). */
	public static Flag group(String field) {
		return Flag.flag("-g").objective(field);
	}
}
