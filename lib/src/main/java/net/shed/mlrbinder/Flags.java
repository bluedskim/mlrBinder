package net.shed.mlrbinder;

/**
 * Static factories for Miller <strong>global</strong> CLI flags.
 * <p>
 * Sourced from Miller upstream
 * <a href="https://github.com/johnkerl/miller/blob/main/docs/src/reference-main-flag-list.md">reference-main-flag-list.md</a>.
 * Regenerate with {@code python3 utils/gen_flags.py}.
 * Use {@link #raw(String)} / {@link #raw(String, String)} for flags not yet listed or added in newer Miller releases.
 * Verb-local options remain on {@link net.shed.mlrbinder.verb.Option} / {@link Flag}.
 * </p>
 * <p>
 * Some flags must be the first tokens after {@code mlr} (e.g. {@code --cpuprofile});
 * add them in the order Miller expects when combining with other flags.
 * </p>
 * <p>
 * Miller options {@code --mfrom} and {@code --mload} take a variable argument list
 * terminated by {@code --}; use {@link Mlr#mfrom(String...)} and {@link Mlr#mload(String...)}.
 * </p>
 */
public final class Flags {
	private Flags() {
	}

	/** Pass-through for a global flag with no separate value token. */
	public static Flag raw(String flagName) {
		return new Flag(flagName);
	}

	/** Pass-through for {@code flagName} then {@code value} (two argv entries). */
	public static Flag raw(String flagName, String value) {
		return new Flag(flagName).objective(value);
	}

	/** Miller `-N`. */
	public static Flag N() {
		return new Flag("-N");
	}

	/** Miller `--allow-ragged-csv-input`. */
	public static Flag allowRaggedCsvInput() {
		return new Flag("--allow-ragged-csv-input");
	}

	/** Miller `--allow-ragged-tsv-input`. */
	public static Flag allowRaggedTsvInput() {
		return new Flag("--allow-ragged-tsv-input");
	}

	/** Miller `--always-color`. */
	public static Flag alwaysColor() {
		return new Flag("--always-color");
	}

	/** Miller `-C`. */
	public static Flag alwaysColorShort() {
		return new Flag("-C");
	}

	/** Miller `--asv`. */
	public static Flag asv() {
		return new Flag("--asv");
	}

	/** Miller `--asvlite`. */
	public static Flag asvlite() {
		return new Flag("--asvlite");
	}

	/** Miller `--barred`. */
	public static Flag barred() {
		return new Flag("--barred");
	}

	/** Miller `--barred-input`. */
	public static Flag barredInput() {
		return new Flag("--barred-input");
	}

	/** Miller `--barred-output`. */
	public static Flag barredOutput() {
		return new Flag("--barred-output");
	}

	/** Miller `--barred-unicode`. */
	public static Flag barredUnicode() {
		return new Flag("--barred-unicode");
	}

	/** Miller `--bz2in`. */
	public static Flag bz2in() {
		return new Flag("--bz2in");
	}

	/** Miller `--c2c`. */
	public static Flag c2c() {
		return new Flag("--c2c");
	}

	/** Miller `--c2d`. */
	public static Flag c2d() {
		return new Flag("--c2d");
	}

	/** Miller `--c2j`. */
	public static Flag c2j() {
		return new Flag("--c2j");
	}

	/** Miller `--c2l`. */
	public static Flag c2l() {
		return new Flag("--c2l");
	}

	/** Miller `--c2m`. */
	public static Flag c2m() {
		return new Flag("--c2m");
	}

	/** Miller `--c2n`. */
	public static Flag c2n() {
		return new Flag("--c2n");
	}

	/** Miller `--c2p`. */
	public static Flag c2p() {
		return new Flag("--c2p");
	}

	/** Miller `--c2t`. */
	public static Flag c2t() {
		return new Flag("--c2t");
	}

	/** Miller `--c2x`. */
	public static Flag c2x() {
		return new Flag("--c2x");
	}

	/** Miller `--c2y`. */
	public static Flag c2y() {
		return new Flag("--c2y");
	}

	/** Miller `-c`. */
	public static Flag cShort() {
		return new Flag("-c");
	}

	/** Miller `--cpuprofile` value. */
	public static Flag cpuprofile(String value) {
		return new Flag("--cpuprofile").objective(value);
	}

	/** Miller `--csv`. */
	public static Flag csv() {
		return new Flag("--csv");
	}

	/** Miller `--csv-trim-leading-space`. */
	public static Flag csvTrimLeadingSpace() {
		return new Flag("--csv-trim-leading-space");
	}

	/** Miller `--csvlite`. */
	public static Flag csvlite() {
		return new Flag("--csvlite");
	}

	/** Miller `--d2c`. */
	public static Flag d2c() {
		return new Flag("--d2c");
	}

	/** Miller `--d2d`. */
	public static Flag d2d() {
		return new Flag("--d2d");
	}

	/** Miller `--d2j`. */
	public static Flag d2j() {
		return new Flag("--d2j");
	}

	/** Miller `--d2l`. */
	public static Flag d2l() {
		return new Flag("--d2l");
	}

	/** Miller `--d2m`. */
	public static Flag d2m() {
		return new Flag("--d2m");
	}

	/** Miller `--d2n`. */
	public static Flag d2n() {
		return new Flag("--d2n");
	}

	/** Miller `--d2p`. */
	public static Flag d2p() {
		return new Flag("--d2p");
	}

	/** Miller `--d2t`. */
	public static Flag d2t() {
		return new Flag("--d2t");
	}

	/** Miller `--d2x`. */
	public static Flag d2x() {
		return new Flag("--d2x");
	}

	/** Miller `--d2y`. */
	public static Flag d2y() {
		return new Flag("--d2y");
	}

	/** Miller `--dcf`. */
	public static Flag dcf() {
		return new Flag("--dcf");
	}

	/** Miller `--dkvp`. */
	public static Flag dkvp() {
		return new Flag("--dkvp");
	}

	/** Miller `--dkvpx`. */
	public static Flag dkvpx() {
		return new Flag("--dkvpx");
	}

	/** Miller `--fail-color` value. */
	public static Flag failColor(String value) {
		return new Flag("--fail-color").objective(value);
	}

	/** Miller `-x`. */
	public static Flag failOnErrorValue() {
		return new Flag("-x");
	}

	/** Miller `--fflush`. */
	public static Flag fflush() {
		return new Flag("--fflush");
	}

	/** Miller `--files` value. */
	public static Flag files(String value) {
		return new Flag("--files").objective(value);
	}

	/** Miller `--fixed` value. */
	public static Flag fixed(String value) {
		return new Flag("--fixed").objective(value);
	}

	/** Miller `-s` value. */
	public static Flag flagsFromFile(String value) {
		return new Flag("-s").objective(value);
	}

	/** Miller `--flatsep` value. */
	public static Flag flatsep(String value) {
		return new Flag("--flatsep").objective(value);
	}

	/** Miller `--from` value. */
	public static Flag from(String value) {
		return new Flag("--from").objective(value);
	}

	/** Miller `--fs` value. */
	public static Flag fs(String value) {
		return new Flag("--fs").objective(value);
	}

	/** Miller `--fw` value. */
	public static Flag fw(String value) {
		return new Flag("--fw").objective(value);
	}

	/** Miller `--gen-field-name` value. */
	public static Flag genFieldName(String value) {
		return new Flag("--gen-field-name").objective(value);
	}

	/** Miller `--gen-start` value. */
	public static Flag genStart(String value) {
		return new Flag("--gen-start").objective(value);
	}

	/** Miller `--gen-step` value. */
	public static Flag genStep(String value) {
		return new Flag("--gen-step").objective(value);
	}

	/** Miller `--gen-stop` value. */
	public static Flag genStop(String value) {
		return new Flag("--gen-stop").objective(value);
	}

	/** Miller `--gzin`. */
	public static Flag gzin() {
		return new Flag("--gzin");
	}

	/** Miller `--hash-records`. */
	public static Flag hashRecords() {
		return new Flag("--hash-records");
	}

	/** Miller `--headerless-csv-input`. */
	public static Flag headerlessCsvInput() {
		return new Flag("--headerless-csv-input");
	}

	/** Miller `--headerless-csv-output`. */
	public static Flag headerlessCsvOutput() {
		return new Flag("--headerless-csv-output");
	}

	/** Miller `--headerless-tsv-output`. */
	public static Flag headerlessTsvOutput() {
		return new Flag("--headerless-tsv-output");
	}

	/** Miller `--help-color` value. */
	public static Flag helpColor(String value) {
		return new Flag("--help-color").objective(value);
	}

	/** Miller `--hi`. */
	public static Flag hi() {
		return new Flag("--hi");
	}

	/** Miller `--ho`. */
	public static Flag ho() {
		return new Flag("--ho");
	}

	/** Miller `--iasv`. */
	public static Flag iasv() {
		return new Flag("--iasv");
	}

	/** Miller `--iasvlite`. */
	public static Flag iasvlite() {
		return new Flag("--iasvlite");
	}

	/** Miller `--icsv`. */
	public static Flag icsv() {
		return new Flag("--icsv");
	}

	/** Miller `--icsvlite`. */
	public static Flag icsvlite() {
		return new Flag("--icsvlite");
	}

	/** Miller `--idcf`. */
	public static Flag idcf() {
		return new Flag("--idcf");
	}

	/** Miller `--idkvp`. */
	public static Flag idkvp() {
		return new Flag("--idkvp");
	}

	/** Miller `--ifs` value. */
	public static Flag ifs(String value) {
		return new Flag("--ifs").objective(value);
	}

	/** Miller `--ifs-regex` value. */
	public static Flag ifsRegex(String value) {
		return new Flag("--ifs-regex").objective(value);
	}

	/** Miller `--igen`. */
	public static Flag igen() {
		return new Flag("--igen");
	}

	/** Miller `--ijson`. */
	public static Flag ijson() {
		return new Flag("--ijson");
	}

	/** Miller `--ijsonl`. */
	public static Flag ijsonl() {
		return new Flag("--ijsonl");
	}

	/** Miller `--imarkdown`. */
	public static Flag imarkdown() {
		return new Flag("--imarkdown");
	}

	/** Miller `--imd`. */
	public static Flag imd() {
		return new Flag("--imd");
	}

	/** Miller `--implicit-csv-header`. */
	public static Flag implicitCsvHeader() {
		return new Flag("--implicit-csv-header");
	}

	/** Miller `--implicit-tsv-header`. */
	public static Flag implicitTsvHeader() {
		return new Flag("--implicit-tsv-header");
	}

	/** Miller `-I`. */
	public static Flag inPlaceShort() {
		return new Flag("-I");
	}

	/** Miller `--incr-key`. */
	public static Flag incrKey() {
		return new Flag("--incr-key");
	}

	/** Miller `--infer-int-as-float`. */
	public static Flag inferIntAsFloat() {
		return new Flag("--infer-int-as-float");
	}

	/** Miller `-A`. */
	public static Flag inferIntAsFloatShort() {
		return new Flag("-A");
	}

	/** Miller `--infer-none`. */
	public static Flag inferNone() {
		return new Flag("--infer-none");
	}

	/** Miller `-S`. */
	public static Flag inferNoneShort() {
		return new Flag("-S");
	}

	/** Miller `--infer-octal`. */
	public static Flag inferOctal() {
		return new Flag("--infer-octal");
	}

	/** Miller `-O`. */
	public static Flag inferOctalShort() {
		return new Flag("-O");
	}

	/** Miller `--inidx`. */
	public static Flag inidx() {
		return new Flag("--inidx");
	}

	/** Miller `-i` value. */
	public static Flag inputFormat(String value) {
		return new Flag("-i").objective(value);
	}

	/** Miller `--io` value. */
	public static Flag io(String value) {
		return new Flag("--io").objective(value);
	}

	/** Miller `--ipprint`. */
	public static Flag ipprint() {
		return new Flag("--ipprint");
	}

	/** Miller `--ips` value. */
	public static Flag ips(String value) {
		return new Flag("--ips").objective(value);
	}

	/** Miller `--ips-regex` value. */
	public static Flag ipsRegex(String value) {
		return new Flag("--ips-regex").objective(value);
	}

	/** Miller `--irs` value. */
	public static Flag irs(String value) {
		return new Flag("--irs").objective(value);
	}

	/** Miller `--itsv`. */
	public static Flag itsv() {
		return new Flag("--itsv");
	}

	/** Miller `--itsvlite`. */
	public static Flag itsvlite() {
		return new Flag("--itsvlite");
	}

	/** Miller `--iusv`. */
	public static Flag iusv() {
		return new Flag("--iusv");
	}

	/** Miller `--iusvlite`. */
	public static Flag iusvlite() {
		return new Flag("--iusvlite");
	}

	/** Miller `--ixtab`. */
	public static Flag ixtab() {
		return new Flag("--ixtab");
	}

	/** Miller `--iyaml`. */
	public static Flag iyaml() {
		return new Flag("--iyaml");
	}

	/** Miller `--j2c`. */
	public static Flag j2c() {
		return new Flag("--j2c");
	}

	/** Miller `--j2d`. */
	public static Flag j2d() {
		return new Flag("--j2d");
	}

	/** Miller `--j2j`. */
	public static Flag j2j() {
		return new Flag("--j2j");
	}

	/** Miller `--j2l`. */
	public static Flag j2l() {
		return new Flag("--j2l");
	}

	/** Miller `--j2m`. */
	public static Flag j2m() {
		return new Flag("--j2m");
	}

	/** Miller `--j2n`. */
	public static Flag j2n() {
		return new Flag("--j2n");
	}

	/** Miller `--j2p`. */
	public static Flag j2p() {
		return new Flag("--j2p");
	}

	/** Miller `--j2t`. */
	public static Flag j2t() {
		return new Flag("--j2t");
	}

	/** Miller `--j2x`. */
	public static Flag j2x() {
		return new Flag("--j2x");
	}

	/** Miller `--j2y`. */
	public static Flag j2y() {
		return new Flag("--j2y");
	}

	/** Miller `-j`. */
	public static Flag jShort() {
		return new Flag("-j");
	}

	/** Miller `--jflatsep` value. */
	public static Flag jflatsep(String value) {
		return new Flag("--jflatsep").objective(value);
	}

	/** Miller `--jknquoteint`. */
	public static Flag jknquoteint() {
		return new Flag("--jknquoteint");
	}

	/** Miller `--jl`. */
	public static Flag jl() {
		return new Flag("--jl");
	}

	/** Miller `--jlistwrap`. */
	public static Flag jlistwrap() {
		return new Flag("--jlistwrap");
	}

	/** Miller `--jquoteall`. */
	public static Flag jquoteall() {
		return new Flag("--jquoteall");
	}

	/** Miller `--json`. */
	public static Flag json() {
		return new Flag("--json");
	}

	/** Miller `--json-fatal-arrays-on-input`. */
	public static Flag jsonFatalArraysOnInput() {
		return new Flag("--json-fatal-arrays-on-input");
	}

	/** Miller `--json-map-arrays-on-input`. */
	public static Flag jsonMapArraysOnInput() {
		return new Flag("--json-map-arrays-on-input");
	}

	/** Miller `--json-skip-arrays-on-input`. */
	public static Flag jsonSkipArraysOnInput() {
		return new Flag("--json-skip-arrays-on-input");
	}

	/** Miller `--jsonl`. */
	public static Flag jsonl() {
		return new Flag("--jsonl");
	}

	/** Miller `--jsonx`. */
	public static Flag jsonx() {
		return new Flag("--jsonx");
	}

	/** Miller `--jvquoteall`. */
	public static Flag jvquoteall() {
		return new Flag("--jvquoteall");
	}

	/** Miller `--jvstack`. */
	public static Flag jvstack() {
		return new Flag("--jvstack");
	}

	/** Miller `--key-color` value. */
	public static Flag keyColor(String value) {
		return new Flag("--key-color").objective(value);
	}

	/** Miller `--l2c`. */
	public static Flag l2c() {
		return new Flag("--l2c");
	}

	/** Miller `--l2d`. */
	public static Flag l2d() {
		return new Flag("--l2d");
	}

	/** Miller `--l2j`. */
	public static Flag l2j() {
		return new Flag("--l2j");
	}

	/** Miller `--l2l`. */
	public static Flag l2l() {
		return new Flag("--l2l");
	}

	/** Miller `--l2m`. */
	public static Flag l2m() {
		return new Flag("--l2m");
	}

	/** Miller `--l2n`. */
	public static Flag l2n() {
		return new Flag("--l2n");
	}

	/** Miller `--l2p`. */
	public static Flag l2p() {
		return new Flag("--l2p");
	}

	/** Miller `--l2t`. */
	public static Flag l2t() {
		return new Flag("--l2t");
	}

	/** Miller `--l2x`. */
	public static Flag l2x() {
		return new Flag("--l2x");
	}

	/** Miller `--l2y`. */
	public static Flag l2y() {
		return new Flag("--l2y");
	}

	/** Miller `--lazy-quotes`. */
	public static Flag lazyQuotes() {
		return new Flag("--lazy-quotes");
	}

	/** Miller `--list-color-codes`. */
	public static Flag listColorCodes() {
		return new Flag("--list-color-codes");
	}

	/** Miller `--list-color-names`. */
	public static Flag listColorNames() {
		return new Flag("--list-color-names");
	}

	/** Miller `--load` value. */
	public static Flag load(String value) {
		return new Flag("--load").objective(value);
	}

	/** Miller `--m2c`. */
	public static Flag m2c() {
		return new Flag("--m2c");
	}

	/** Miller `--m2d`. */
	public static Flag m2d() {
		return new Flag("--m2d");
	}

	/** Miller `--m2j`. */
	public static Flag m2j() {
		return new Flag("--m2j");
	}

	/** Miller `--m2l`. */
	public static Flag m2l() {
		return new Flag("--m2l");
	}

	/** Miller `--m2n`. */
	public static Flag m2n() {
		return new Flag("--m2n");
	}

	/** Miller `--m2p`. */
	public static Flag m2p() {
		return new Flag("--m2p");
	}

	/** Miller `--m2t`. */
	public static Flag m2t() {
		return new Flag("--m2t");
	}

	/** Miller `--m2x`. */
	public static Flag m2x() {
		return new Flag("--m2x");
	}

	/** Miller `--m2y`. */
	public static Flag m2y() {
		return new Flag("--m2y");
	}

	/** Miller `--mmap`. */
	public static Flag mmap() {
		return new Flag("--mmap");
	}

	/** Miller `--n2c`. */
	public static Flag n2c() {
		return new Flag("--n2c");
	}

	/** Miller `--n2d`. */
	public static Flag n2d() {
		return new Flag("--n2d");
	}

	/** Miller `--n2j`. */
	public static Flag n2j() {
		return new Flag("--n2j");
	}

	/** Miller `--n2l`. */
	public static Flag n2l() {
		return new Flag("--n2l");
	}

	/** Miller `--n2m`. */
	public static Flag n2m() {
		return new Flag("--n2m");
	}

	/** Miller `--n2n`. */
	public static Flag n2n() {
		return new Flag("--n2n");
	}

	/** Miller `--n2p`. */
	public static Flag n2p() {
		return new Flag("--n2p");
	}

	/** Miller `--n2t`. */
	public static Flag n2t() {
		return new Flag("--n2t");
	}

	/** Miller `--n2x`. */
	public static Flag n2x() {
		return new Flag("--n2x");
	}

	/** Miller `--n2y`. */
	public static Flag n2y() {
		return new Flag("--n2y");
	}

	/** Miller `--nidx`. */
	public static Flag nidx() {
		return new Flag("--nidx");
	}

	/** Miller `--no-auto-flatten`. */
	public static Flag noAutoFlatten() {
		return new Flag("--no-auto-flatten");
	}

	/** Miller `--no-auto-unflatten`. */
	public static Flag noAutoUnflatten() {
		return new Flag("--no-auto-unflatten");
	}

	/** Miller `--no-auto-unsparsify`. */
	public static Flag noAutoUnsparsify() {
		return new Flag("--no-auto-unsparsify");
	}

	/** Miller `--no-color`. */
	public static Flag noColor() {
		return new Flag("--no-color");
	}

	/** Miller `-M`. */
	public static Flag noColorShort() {
		return new Flag("-M");
	}

	/** Miller `--no-dedupe-field-names`. */
	public static Flag noDedupeFieldNames() {
		return new Flag("--no-dedupe-field-names");
	}

	/** Miller `--no-fflush`. */
	public static Flag noFflush() {
		return new Flag("--no-fflush");
	}

	/** Miller `--no-hash-records`. */
	public static Flag noHashRecords() {
		return new Flag("--no-hash-records");
	}

	/** Miller `--no-implicit-csv-header`. */
	public static Flag noImplicitCsvHeader() {
		return new Flag("--no-implicit-csv-header");
	}

	/** Miller `--no-implicit-tsv-header`. */
	public static Flag noImplicitTsvHeader() {
		return new Flag("--no-implicit-tsv-header");
	}

	/** Miller `-n`. */
	public static Flag noInput() {
		return new Flag("-n");
	}

	/** Miller `--no-jlistwrap`. */
	public static Flag noJlistwrap() {
		return new Flag("--no-jlistwrap");
	}

	/** Miller `--no-jvstack`. */
	public static Flag noJvstack() {
		return new Flag("--no-jvstack");
	}

	/** Miller `--no-mmap`. */
	public static Flag noMmap() {
		return new Flag("--no-mmap");
	}

	/** Miller `--no-yarray`. */
	public static Flag noYarray() {
		return new Flag("--no-yarray");
	}

	/** Miller `--norc`. */
	public static Flag norc() {
		return new Flag("--norc");
	}

	/** Miller `--nr-progress-mod` value. */
	public static Flag nrProgressMod(String value) {
		return new Flag("--nr-progress-mod").objective(value);
	}

	/** Miller `--oasv`. */
	public static Flag oasv() {
		return new Flag("--oasv");
	}

	/** Miller `--oasvlite`. */
	public static Flag oasvlite() {
		return new Flag("--oasvlite");
	}

	/** Miller `--ocsv`. */
	public static Flag ocsv() {
		return new Flag("--ocsv");
	}

	/** Miller `--ocsvlite`. */
	public static Flag ocsvlite() {
		return new Flag("--ocsvlite");
	}

	/** Miller `--odcf`. */
	public static Flag odcf() {
		return new Flag("--odcf");
	}

	/** Miller `--odkvp`. */
	public static Flag odkvp() {
		return new Flag("--odkvp");
	}

	/** Miller `--ofmt` value. */
	public static Flag ofmt(String value) {
		return new Flag("--ofmt").objective(value);
	}

	/** Miller `--ofmte` value. */
	public static Flag ofmte(String value) {
		return new Flag("--ofmte").objective(value);
	}

	/** Miller `--ofmtf` value. */
	public static Flag ofmtf(String value) {
		return new Flag("--ofmtf").objective(value);
	}

	/** Miller `--ofmtg` value. */
	public static Flag ofmtg(String value) {
		return new Flag("--ofmtg").objective(value);
	}

	/** Miller `--ofs` value. */
	public static Flag ofs(String value) {
		return new Flag("--ofs").objective(value);
	}

	/** Miller `--ojson`. */
	public static Flag ojson() {
		return new Flag("--ojson");
	}

	/** Miller `--ojsonl`. */
	public static Flag ojsonl() {
		return new Flag("--ojsonl");
	}

	/** Miller `--ojsonx`. */
	public static Flag ojsonx() {
		return new Flag("--ojsonx");
	}

	/** Miller `--omarkdown`. */
	public static Flag omarkdown() {
		return new Flag("--omarkdown");
	}

	/** Miller `--omd`. */
	public static Flag omd() {
		return new Flag("--omd");
	}

	/** Miller `--onidx`. */
	public static Flag onidx() {
		return new Flag("--onidx");
	}

	/** Miller `--opprint`. */
	public static Flag opprint() {
		return new Flag("--opprint");
	}

	/** Miller `--ops` value. */
	public static Flag ops(String value) {
		return new Flag("--ops").objective(value);
	}

	/** Miller `--ors` value. */
	public static Flag ors(String value) {
		return new Flag("--ors").objective(value);
	}

	/** Miller `--otsv`. */
	public static Flag otsv() {
		return new Flag("--otsv");
	}

	/** Miller `--otsvlite`. */
	public static Flag otsvlite() {
		return new Flag("--otsvlite");
	}

	/** Miller `--ousv`. */
	public static Flag ousv() {
		return new Flag("--ousv");
	}

	/** Miller `--ousvlite`. */
	public static Flag ousvlite() {
		return new Flag("--ousvlite");
	}

	/** Miller `-o` value. */
	public static Flag outputFormat(String value) {
		return new Flag("-o").objective(value);
	}

	/** Miller `--oxtab`. */
	public static Flag oxtab() {
		return new Flag("--oxtab");
	}

	/** Miller `--oyaml`. */
	public static Flag oyaml() {
		return new Flag("--oyaml");
	}

	/** Miller `--p2c`. */
	public static Flag p2c() {
		return new Flag("--p2c");
	}

	/** Miller `--p2d`. */
	public static Flag p2d() {
		return new Flag("--p2d");
	}

	/** Miller `--p2j`. */
	public static Flag p2j() {
		return new Flag("--p2j");
	}

	/** Miller `--p2l`. */
	public static Flag p2l() {
		return new Flag("--p2l");
	}

	/** Miller `--p2m`. */
	public static Flag p2m() {
		return new Flag("--p2m");
	}

	/** Miller `--p2n`. */
	public static Flag p2n() {
		return new Flag("--p2n");
	}

	/** Miller `--p2p`. */
	public static Flag p2p() {
		return new Flag("--p2p");
	}

	/** Miller `--p2t`. */
	public static Flag p2t() {
		return new Flag("--p2t");
	}

	/** Miller `--p2x`. */
	public static Flag p2x() {
		return new Flag("--p2x");
	}

	/** Miller `--p2y`. */
	public static Flag p2y() {
		return new Flag("--p2y");
	}

	/** Miller `--pass-color` value. */
	public static Flag passColor(String value) {
		return new Flag("--pass-color").objective(value);
	}

	/** Miller `--pass-comments`. */
	public static Flag passComments() {
		return new Flag("--pass-comments");
	}

	/** Miller `--pass-comments-with` value. */
	public static Flag passCommentsWith(String value) {
		return new Flag("--pass-comments-with").objective(value);
	}

	/** Miller `--pprint`. */
	public static Flag pprint() {
		return new Flag("--pprint");
	}

	/** Miller `--prepipe` value. */
	public static Flag prepipe(String value) {
		return new Flag("--prepipe").objective(value);
	}

	/** Miller `--prepipe-bz2`. */
	public static Flag prepipeBz2() {
		return new Flag("--prepipe-bz2");
	}

	/** Miller `--prepipe-gunzip`. */
	public static Flag prepipeGunzip() {
		return new Flag("--prepipe-gunzip");
	}

	/** Miller `--prepipe-zcat`. */
	public static Flag prepipeZcat() {
		return new Flag("--prepipe-zcat");
	}

	/** Miller `--prepipe-zstdcat`. */
	public static Flag prepipeZstdcat() {
		return new Flag("--prepipe-zstdcat");
	}

	/** Miller `--prepipex` value. */
	public static Flag prepipex(String value) {
		return new Flag("--prepipex").objective(value);
	}

	/** Miller `--ps` value. */
	public static Flag ps(String value) {
		return new Flag("--ps").objective(value);
	}

	/** Miller `--quote-all`. */
	public static Flag quoteAll() {
		return new Flag("--quote-all");
	}

	/** Miller `--quote-minimal`. */
	public static Flag quoteMinimal() {
		return new Flag("--quote-minimal");
	}

	/** Miller `--quote-none`. */
	public static Flag quoteNone() {
		return new Flag("--quote-none");
	}

	/** Miller `--quote-numeric`. */
	public static Flag quoteNumeric() {
		return new Flag("--quote-numeric");
	}

	/** Miller `--quote-original`. */
	public static Flag quoteOriginal() {
		return new Flag("--quote-original");
	}

	/** Miller `--ragged`. */
	public static Flag ragged() {
		return new Flag("--ragged");
	}

	/** Miller `--records-per-batch` value. */
	public static Flag recordsPerBatch(String value) {
		return new Flag("--records-per-batch").objective(value);
	}

	/** Miller `--repifs`. */
	public static Flag repifs() {
		return new Flag("--repifs");
	}

	/** Miller `--right`. */
	public static Flag right() {
		return new Flag("--right");
	}

	/** Miller `--rs` value. */
	public static Flag rs(String value) {
		return new Flag("--rs").objective(value);
	}

	/** Miller `--s-no-comment-strip` value. */
	public static Flag sNoCommentStrip(String value) {
		return new Flag("--s-no-comment-strip").objective(value);
	}

	/** Miller `--seed` value. */
	public static Flag seed(String value) {
		return new Flag("--seed").objective(value);
	}

	/** Miller `--skip-comments`. */
	public static Flag skipComments() {
		return new Flag("--skip-comments");
	}

	/** Miller `--skip-comments-with` value. */
	public static Flag skipCommentsWith(String value) {
		return new Flag("--skip-comments-with").objective(value);
	}

	/** Miller `--t2c`. */
	public static Flag t2c() {
		return new Flag("--t2c");
	}

	/** Miller `--t2d`. */
	public static Flag t2d() {
		return new Flag("--t2d");
	}

	/** Miller `--t2j`. */
	public static Flag t2j() {
		return new Flag("--t2j");
	}

	/** Miller `--t2l`. */
	public static Flag t2l() {
		return new Flag("--t2l");
	}

	/** Miller `--t2m`. */
	public static Flag t2m() {
		return new Flag("--t2m");
	}

	/** Miller `--t2n`. */
	public static Flag t2n() {
		return new Flag("--t2n");
	}

	/** Miller `--t2p`. */
	public static Flag t2p() {
		return new Flag("--t2p");
	}

	/** Miller `--t2t`. */
	public static Flag t2t() {
		return new Flag("--t2t");
	}

	/** Miller `--t2x`. */
	public static Flag t2x() {
		return new Flag("--t2x");
	}

	/** Miller `--t2y`. */
	public static Flag t2y() {
		return new Flag("--t2y");
	}

	/** Miller `-t`. */
	public static Flag tShort() {
		return new Flag("-t");
	}

	/** Miller `--time`. */
	public static Flag time() {
		return new Flag("--time");
	}

	/** Miller `--traceprofile`. */
	public static Flag traceprofile() {
		return new Flag("--traceprofile");
	}

	/** Miller `--tsv`. */
	public static Flag tsv() {
		return new Flag("--tsv");
	}

	/** Miller `--tsvlite`. */
	public static Flag tsvlite() {
		return new Flag("--tsvlite");
	}

	/** Miller `--tz` value. */
	public static Flag tz(String value) {
		return new Flag("--tz").objective(value);
	}

	/** Miller `--usv`. */
	public static Flag usv() {
		return new Flag("--usv");
	}

	/** Miller `--usvlite`. */
	public static Flag usvlite() {
		return new Flag("--usvlite");
	}

	/** Miller `--value-color` value. */
	public static Flag valueColor(String value) {
		return new Flag("--value-color").objective(value);
	}

	/** Miller `--vflatsep`. */
	public static Flag vflatsep() {
		return new Flag("--vflatsep");
	}

	/** Miller `--x2c`. */
	public static Flag x2c() {
		return new Flag("--x2c");
	}

	/** Miller `--x2d`. */
	public static Flag x2d() {
		return new Flag("--x2d");
	}

	/** Miller `--x2j`. */
	public static Flag x2j() {
		return new Flag("--x2j");
	}

	/** Miller `--x2l`. */
	public static Flag x2l() {
		return new Flag("--x2l");
	}

	/** Miller `--x2m`. */
	public static Flag x2m() {
		return new Flag("--x2m");
	}

	/** Miller `--x2n`. */
	public static Flag x2n() {
		return new Flag("--x2n");
	}

	/** Miller `--x2p`. */
	public static Flag x2p() {
		return new Flag("--x2p");
	}

	/** Miller `--x2t`. */
	public static Flag x2t() {
		return new Flag("--x2t");
	}

	/** Miller `--x2x`. */
	public static Flag x2x() {
		return new Flag("--x2x");
	}

	/** Miller `--x2y`. */
	public static Flag x2y() {
		return new Flag("--x2y");
	}

	/** Miller `--xtab`. */
	public static Flag xtab() {
		return new Flag("--xtab");
	}

	/** Miller `--xvright`. */
	public static Flag xvright() {
		return new Flag("--xvright");
	}

	/** Miller `--y2c`. */
	public static Flag y2c() {
		return new Flag("--y2c");
	}

	/** Miller `--y2d`. */
	public static Flag y2d() {
		return new Flag("--y2d");
	}

	/** Miller `--y2j`. */
	public static Flag y2j() {
		return new Flag("--y2j");
	}

	/** Miller `--y2l`. */
	public static Flag y2l() {
		return new Flag("--y2l");
	}

	/** Miller `--y2m`. */
	public static Flag y2m() {
		return new Flag("--y2m");
	}

	/** Miller `--y2n`. */
	public static Flag y2n() {
		return new Flag("--y2n");
	}

	/** Miller `--y2p`. */
	public static Flag y2p() {
		return new Flag("--y2p");
	}

	/** Miller `--y2t`. */
	public static Flag y2t() {
		return new Flag("--y2t");
	}

	/** Miller `--y2x`. */
	public static Flag y2x() {
		return new Flag("--y2x");
	}

	/** Miller `--y2y`. */
	public static Flag y2y() {
		return new Flag("--y2y");
	}

	/** Miller `--ya`. */
	public static Flag ya() {
		return new Flag("--ya");
	}

	/** Miller `--yaml`. */
	public static Flag yaml() {
		return new Flag("--yaml");
	}

	/** Miller `--yarray`. */
	public static Flag yarray() {
		return new Flag("--yarray");
	}

	/** Miller `--zin`. */
	public static Flag zin() {
		return new Flag("--zin");
	}

	/** Miller `--zstdin`. */
	public static Flag zstdin() {
		return new Flag("--zstdin");
	}

}