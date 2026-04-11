package net.shed.mlrbinder;

/**
 * Shorthand {@link Flag} factories for Miller {@code stats1} / {@code stats2} style options.
 */
public final class StatsFlags {
	private StatsFlags() {
	}

	/** Miller {@code -a} with a comma-separated aggregation list. */
	public static Flag aggregations(String list) {
		return Flag.flag("-a").objective(list);
	}

	/** Miller {@code -f} field name (or list where Miller allows). */
	public static Flag field(String name) {
		return Flag.flag("-f").objective(name);
	}

	/** Miller {@code -g} group-by field(s), comma-separated if multiple. */
	public static Flag groupBy(String fields) {
		return Flag.flag("-g").objective(fields);
	}
}
