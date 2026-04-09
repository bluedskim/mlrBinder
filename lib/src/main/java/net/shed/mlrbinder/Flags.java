package net.shed.mlrbinder;

/**
 * Prebuilt {@link Flag} factories for common Miller I/O and format options (v0.02).
 * <p>
 * This is not an exhaustive list of Miller global flags; for anything else use {@link Flag#Flag(String)} (or add a
 * factory here if it is widely reused).
 * </p>
 */
public final class Flags {
	private Flags() {
	}

	public static Flag csv() {
		return new Flag("--csv");
	}

	public static Flag tsv() {
		return new Flag("--tsv");
	}

	public static Flag json() {
		return new Flag("--json");
	}

	public static Flag jsonl() {
		return new Flag("--jsonl");
	}

	public static Flag dkvp() {
		return new Flag("--dkvp");
	}

	public static Flag xtab() {
		return new Flag("--xtab");
	}

	public static Flag pprint() {
		return new Flag("--pprint");
	}

	public static Flag nidx() {
		return new Flag("--nidx");
	}

	public static Flag icsv() {
		return new Flag("--icsv");
	}

	public static Flag ocsv() {
		return new Flag("--ocsv");
	}

	public static Flag itsv() {
		return new Flag("--itsv");
	}

	public static Flag otsv() {
		return new Flag("--otsv");
	}

	public static Flag ijson() {
		return new Flag("--ijson");
	}

	public static Flag ojson() {
		return new Flag("--ojson");
	}

	public static Flag idkvp() {
		return new Flag("--idkvp");
	}

	public static Flag odkvp() {
		return new Flag("--odkvp");
	}

	public static Flag ixtab() {
		return new Flag("--ixtab");
	}

	public static Flag oxtab() {
		return new Flag("--oxtab");
	}

	public static Flag ipprint() {
		return new Flag("--ipprint");
	}

	public static Flag opprint() {
		return new Flag("--opprint");
	}

	public static Flag inidx() {
		return new Flag("--inidx");
	}

	public static Flag onidx() {
		return new Flag("--onidx");
	}
}
