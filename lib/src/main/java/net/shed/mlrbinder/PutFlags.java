package net.shed.mlrbinder;

/**
 * Shorthand {@link Flag} factories for Miller {@code put}.
 */
public final class PutFlags {
	private PutFlags() {
	}

	/** Miller {@code put -q} (quiet: suppress per-record statistics). */
	public static Flag quiet() {
		return Flag.flag("-q");
	}
}
