package net.shed.mlrbinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import net.shed.mlrbinder.Arg;
import net.shed.mlrbinder.Objective;
import net.shed.mlrbinder.verb.Option;
import net.shed.mlrbinder.verb.Verb;

/**
 * Miller command builder and runner: executable path + global {@link Flag}s + optional {@code --mfrom}/{@code --mload}
 * tokens + chained {@link net.shed.mlrbinder.verb.Verb}s + input files, then {@link #run()} or {@link #run(InputStreamReader)}.
 * <p>
 * <strong>Recommended style:</strong> chain global I/O flags on {@code Mlr} (e.g. {@link #icsv()}, {@link #from(String)}),
 * then append each Miller verb with the <strong>same-named instance method</strong> (each delegates to
 * {@link Mlr.Verbs}). Global flags that share a prefix with common Miller options use a {@code *Flag} name for IDE
 * grouping (e.g. {@link #csvFlag}, {@link #jsonFlag}). Use {@link #filterVerb} / {@link #splitVerb} for Miller {@code filter} / {@code split} so they do not
 * collide with {@link java.util.stream.Stream#filter}. Prefer this over {@link #flag(Flag)} plus {@link #verb(Verb...)}
 * when the fluent surface covers your case; fall back to {@code .verb(Mlr.Verbs.foo(…))} only when needed.
 * </p>
 * <p>
 * Start builds with {@link #inDir(String)}, {@link #csv()}, or {@link #mlr()} rather than raw constructors when possible.
 * </p>
 */
public final class Mlr {
	/**
	 * put spacer between words
	 */
	public static final String SPACER = " ";
	public static final String DEFAULT_MLR_PATH = "mlr";
	/**
	 * verbs can be chained with CHAINING_ADVERB
	 */
	public static final String CHAINING_ADVERB = "then";

	private ProcessBuilder processBuilder;

	private static Logger logger = Logger.getLogger(Mlr.class.getName());

	/** {@code mlr} on {@code PATH} with process cwd {@code workingDirectory}. */
	public static Mlr inDir(String workingDirectory) {
		Objects.requireNonNull(workingDirectory, "workingDirectory");
		return new Mlr(DEFAULT_MLR_PATH, workingDirectory);
	}

	/** Same as {@link #inDir(String)}. */
	public static Mlr inDir(File directory) {
		Objects.requireNonNull(directory, "directory");
		return inDir(directory.getPath());
	}

	/** {@code mlr} on PATH; set {@link #workingPath(String)} (or use {@link #file(File)}) before {@link #run()}. */
	public static Mlr mlr() {
		return new Mlr();
	}

	/** Custom {@code mlr} binary; cwd must still be set before run unless supplied by {@link #file(File)}. */
	public static Mlr binary(String mlrExecutablePath) {
		return new Mlr(Objects.requireNonNull(mlrExecutablePath, "mlrExecutablePath"));
	}

	/** Test hook: supply a {@link ProcessBuilder} used for {@link #run()} / {@link #run(InputStreamReader)}. */
	public static Mlr withProcessBuilder(ProcessBuilder processBuilder) {
		return new Mlr(processBuilder);
	}

	Integer exitCode;
	String stdErr;
	private String mlrPath;

	/**
	 * default constructor
	 */
	public Mlr() {
		super();
		mlrPath = DEFAULT_MLR_PATH;
		processBuilder = new ProcessBuilder();
	}

	/**
	 * constructor, get ProcessBuilder
	 * @param mlrPath
	 */
	public Mlr(ProcessBuilder processBuilder) {
		this();
		this.processBuilder = processBuilder;
	}

	/**
	 * constructor, get mlr executable path
	 * @param mlrPath
	 */
	public Mlr(String mlrPath) {
		this();
		this.mlrPath = mlrPath;
	}

	/**
	 * constructor, get mlr executable path
	 * @param mlrPath
	 */
	public Mlr(String mlrPath, String workingPath) {
		this();
		this.mlrPath = mlrPath;
		this.workingPath = workingPath;
	}

	/**
	 * set executable path then return self
	 * @param mlrPath
	 * @return
	 */
	public Mlr mlrPath(String mlrPath) {
		this.mlrPath = mlrPath;
		return this;
	}

	/**
	 * return mlr path
	 * @return
	 */
	public String getMlrPath() {
		return mlrPath;
	}

	String workingPath;

	/**
	 * set executable path then return self
	 * @param mlrPath
	 * @return
	 */
	public Mlr workingPath(String workingPath) {
		this.workingPath = workingPath;
		return this;
	}

	private File redirectOutputFile;

	public Mlr redirectOutputFile(File redirectOutputFile) {
		this.redirectOutputFile = redirectOutputFile;
		return this;
	}

	public File getRedirectOutputFile() {
		return redirectOutputFile;
	}

	/**
	 * list of flags
	 */
	private List<Flag> flags = new ArrayList<>();

	/**
	 * return flag list
	 * @return
	 */
	public List<Flag> getFlags() {
		return flags;
	}

	/**
	 * add flag
	 * @param flag
	 * @return this Mlr
	 */
	public Mlr flag(Flag flag) {
		flags.add(flag);
		return this;
	}

	/**
	 * list of verbs
	 */
	private List<Verb> verbs = new ArrayList<>();

	/**
	 * Tokens after global {@link Flag}s and before the first verb: used for Miller {@code --mfrom} / {@code --mload}
	 * (each is {@code flag arg1 arg2 ... --}).
	 */
	private final List<String> preVerbArgs = new ArrayList<>();

	/**
	 * return list of verbs
	 * @return verbs
	 */
	public List<Verb> getVerbs() {
		return verbs;
	}

	/**
	 * Miller {@code --mfrom} with a variable file list, terminated by {@code --} on the argv.
	 */
	public Mlr mfrom(String... paths) {
		Objects.requireNonNull(paths, "paths");
		preVerbArgs.add("--mfrom");
		for (String p : paths) {
			preVerbArgs.add(Objects.requireNonNull(p, "path"));
		}
		preVerbArgs.add("--");
		return this;
	}

	/**
	 * Miller {@code --mload} with a variable script list, terminated by {@code --} on the argv.
	 */
	public Mlr mload(String... scripts) {
		Objects.requireNonNull(scripts, "scripts");
		preVerbArgs.add("--mload");
		for (String s : scripts) {
			preVerbArgs.add(Objects.requireNonNull(s, "script"));
		}
		preVerbArgs.add("--");
		return this;
	}

	/**
	 * Arguments inserted after flags and before verbs (unmodifiable view).
	 */
	public List<String> getPreVerbArgs() {
		return Collections.unmodifiableList(preVerbArgs);
	}

	/**
	 * add verbs and return this Mlr
	 * @param flag
	 * @return this Mlr
	 */
	public Mlr verb(Verb... verbs) {
		this.verbs.addAll(Arrays.asList(verbs));
		return this;
	}

	/**
	 * list of file names
	 */
	private List<String> fileNames = new ArrayList<>();

	public Mlr file(String fileName) {
		fileNames.add(fileName);
		return this;
	}

	/**
	 * Adds an input file argument. If {@link #workingPath} is not set yet: for a relative file, defaults the process
	 * working directory to {@code user.dir}; for an absolute file, sets it to the file's parent directory (file name
	 * only is passed to {@code mlr}, matching {@link #file(String)} with a bare name and matching working directory).
	 */
	public Mlr file(File file) {
		Objects.requireNonNull(file, "file");
		File absolute = file.getAbsoluteFile();
		if (file.isAbsolute()) {
			File parent = absolute.getParentFile();
			if (workingPath == null) {
				workingPath = parent != null ? parent.getPath() : System.getProperty("user.dir");
			}
			fileNames.add(absolute.getName());
		} else {
			if (workingPath == null) {
				workingPath = System.getProperty("user.dir");
			}
			fileNames.add(file.getPath());
		}
		return this;
	}

	/**
	 * Fluent entry: {@code mlr} on {@code PATH}, {@code --csv}, empty verbs/files until chained.
	 */
	public static Mlr csv() {
		return mlr().flag(Flags.csv());
	}

	/**
	 * Alias for {@link #workingPath(String)} for fluent chains.
	 */
	public Mlr workDir(String path) {
		return workingPath(path);
	}

	/**
	 * Sets the process working directory from {@code dir}'s path.
	 */
	public Mlr workDir(File dir) {
		Objects.requireNonNull(dir, "dir");
		return workingPath(dir.getPath());
	}

	// --- Miller verbs (fluent; each delegates to {@link Mlr.Verbs}) ---

	/** Appends Miller verb <code>altkv</code>; delegates to {@link Mlr.Verbs#altkv}. */
	public Mlr altkv(Arg... args) {
		return verb(Mlr.Verbs.altkv(args));
	}

	/** Appends Miller verb <code>bar</code>; delegates to {@link Mlr.Verbs#bar}. */
	public Mlr bar(Arg... args) {
		return verb(Mlr.Verbs.bar(args));
	}

	/** Appends Miller verb <code>bootstrap</code>; delegates to {@link Mlr.Verbs#bootstrap}. */
	public Mlr bootstrap(Arg... args) {
		return verb(Mlr.Verbs.bootstrap(args));
	}

	/** Appends Miller verb <code>case</code>; delegates to {@link Mlr.Verbs#caseVerb}. */
	public Mlr caseVerb(Arg... args) {
		return verb(Mlr.Verbs.caseVerb(args));
	}

	/** Appends Miller verb <code>cat</code>; delegates to {@link Mlr.Verbs#cat}. */
	public Mlr cat(Arg... args) {
		return verb(Mlr.Verbs.cat(args));
	}

	/** Appends Miller verb <code>check</code>; delegates to {@link Mlr.Verbs#check}. */
	public Mlr check(Arg... args) {
		return verb(Mlr.Verbs.check(args));
	}

	/** Appends Miller verb <code>cleanWhitespace</code>; delegates to {@link Mlr.Verbs#cleanWhitespace}. */
	public Mlr cleanWhitespace(Arg... args) {
		return verb(Mlr.Verbs.cleanWhitespace(args));
	}

	/** Appends Miller verb <code>count</code>; delegates to {@link Mlr.Verbs#count}. */
	public Mlr count(Arg... args) {
		return verb(Mlr.Verbs.count(args));
	}

	/** Appends Miller verb <code>countDistinct</code>; delegates to {@link Mlr.Verbs#countDistinct}. */
	public Mlr countDistinct(Arg... args) {
		return verb(Mlr.Verbs.countDistinct(args));
	}

	/** Appends Miller verb <code>countSimilar</code>; delegates to {@link Mlr.Verbs#countSimilar}. */
	public Mlr countSimilar(Arg... args) {
		return verb(Mlr.Verbs.countSimilar(args));
	}

	/** Appends Miller verb <code>cut</code>; delegates to {@link Mlr.Verbs#cut}. */
	public Mlr cut(Arg... args) {
		return verb(Mlr.Verbs.cut(args));
	}

	/** Appends Miller verb <code>decimate</code>; delegates to {@link Mlr.Verbs#decimate}. */
	public Mlr decimate(Arg... args) {
		return verb(Mlr.Verbs.decimate(args));
	}

	/** Appends Miller verb <code>fillDown</code>; delegates to {@link Mlr.Verbs#fillDown}. */
	public Mlr fillDown(Arg... args) {
		return verb(Mlr.Verbs.fillDown(args));
	}

	/** Appends Miller verb <code>fillEmpty</code>; delegates to {@link Mlr.Verbs#fillEmpty}. */
	public Mlr fillEmpty(Arg... args) {
		return verb(Mlr.Verbs.fillEmpty(args));
	}

	/** Appends Miller verb <code>filter</code>; delegates to {@link Mlr.Verbs#filter}. */
	public Mlr filterVerb(Arg... args) {
		return verb(Mlr.Verbs.filter(args));
	}

	/** Appends Miller verb <code>flatten</code>; delegates to {@link Mlr.Verbs#flatten}. */
	public Mlr flatten(Arg... args) {
		return verb(Mlr.Verbs.flatten(args));
	}

	/** Appends Miller verb <code>formatValues</code>; delegates to {@link Mlr.Verbs#formatValues}. */
	public Mlr formatValues(Arg... args) {
		return verb(Mlr.Verbs.formatValues(args));
	}

	/** Appends Miller verb <code>fraction</code>; delegates to {@link Mlr.Verbs#fraction}. */
	public Mlr fraction(Arg... args) {
		return verb(Mlr.Verbs.fraction(args));
	}

	/** Appends Miller verb <code>gap</code>; delegates to {@link Mlr.Verbs#gap}. */
	public Mlr gap(Arg... args) {
		return verb(Mlr.Verbs.gap(args));
	}

	/** Appends Miller verb <code>grep</code>; delegates to {@link Mlr.Verbs#grep}. */
	public Mlr grep(Arg... args) {
		return verb(Mlr.Verbs.grep(args));
	}

	/** Appends Miller verb <code>gsub</code>; delegates to {@link Mlr.Verbs#gsub}. */
	public Mlr gsub(Arg... args) {
		return verb(Mlr.Verbs.gsub(args));
	}

	/** Appends Miller verb <code>groupBy</code>; delegates to {@link Mlr.Verbs#groupBy}. */
	public Mlr groupBy(Arg... args) {
		return verb(Mlr.Verbs.groupBy(args));
	}

	/** Appends Miller verb <code>groupLike</code>; delegates to {@link Mlr.Verbs#groupLike}. */
	public Mlr groupLike(Arg... args) {
		return verb(Mlr.Verbs.groupLike(args));
	}

	/** Appends Miller verb <code>havingFields</code>; delegates to {@link Mlr.Verbs#havingFields}. */
	public Mlr havingFields(Arg... args) {
		return verb(Mlr.Verbs.havingFields(args));
	}

	/** Appends Miller verb <code>head</code>; delegates to {@link Mlr.Verbs#head}. */
	public Mlr head(Arg... args) {
		return verb(Mlr.Verbs.head(args));
	}

	/** Appends Miller verb <code>histogram</code>; delegates to {@link Mlr.Verbs#histogram}. */
	public Mlr histogram(Arg... args) {
		return verb(Mlr.Verbs.histogram(args));
	}

	/** Appends Miller verb <code>join</code>; delegates to {@link Mlr.Verbs#join}. */
	public Mlr join(Arg... args) {
		return verb(Mlr.Verbs.join(args));
	}

	/** Appends Miller verb <code>jsonParse</code>; delegates to {@link Mlr.Verbs#jsonParse}. */
	public Mlr jsonParse(Arg... args) {
		return verb(Mlr.Verbs.jsonParse(args));
	}

	/** Appends Miller verb <code>jsonStringify</code>; delegates to {@link Mlr.Verbs#jsonStringify}. */
	public Mlr jsonStringify(Arg... args) {
		return verb(Mlr.Verbs.jsonStringify(args));
	}

	/** Appends Miller verb <code>label</code>; delegates to {@link Mlr.Verbs#label}. */
	public Mlr label(Arg... args) {
		return verb(Mlr.Verbs.label(args));
	}

	/** Appends Miller verb <code>latin1ToUtf8</code>; delegates to {@link Mlr.Verbs#latin1ToUtf8}. */
	public Mlr latin1ToUtf8(Arg... args) {
		return verb(Mlr.Verbs.latin1ToUtf8(args));
	}

	/** Appends Miller verb <code>utf8ToLatin1</code>; delegates to {@link Mlr.Verbs#utf8ToLatin1}. */
	public Mlr utf8ToLatin1(Arg... args) {
		return verb(Mlr.Verbs.utf8ToLatin1(args));
	}

	/** Appends Miller verb <code>leastFrequent</code>; delegates to {@link Mlr.Verbs#leastFrequent}. */
	public Mlr leastFrequent(Arg... args) {
		return verb(Mlr.Verbs.leastFrequent(args));
	}

	/** Appends Miller verb <code>mergeFields</code>; delegates to {@link Mlr.Verbs#mergeFields}. */
	public Mlr mergeFields(Arg... args) {
		return verb(Mlr.Verbs.mergeFields(args));
	}

	/** Appends Miller verb <code>mostFrequent</code>; delegates to {@link Mlr.Verbs#mostFrequent}. */
	public Mlr mostFrequent(Arg... args) {
		return verb(Mlr.Verbs.mostFrequent(args));
	}

	/** Appends Miller verb <code>nest</code>; delegates to {@link Mlr.Verbs#nest}. */
	public Mlr nest(Arg... args) {
		return verb(Mlr.Verbs.nest(args));
	}

	/** Appends Miller verb <code>nothing</code>; delegates to {@link Mlr.Verbs#nothing}. */
	public Mlr nothing(Arg... args) {
		return verb(Mlr.Verbs.nothing(args));
	}

	/** Appends Miller verb <code>put</code>; delegates to {@link Mlr.Verbs#put}. */
	public Mlr put(Arg... args) {
		return verb(Mlr.Verbs.put(args));
	}

	/** Appends Miller verb <code>regularize</code>; delegates to {@link Mlr.Verbs#regularize}. */
	public Mlr regularize(Arg... args) {
		return verb(Mlr.Verbs.regularize(args));
	}

	/** Appends Miller verb <code>removeEmptyColumns</code>; delegates to {@link Mlr.Verbs#removeEmptyColumns}. */
	public Mlr removeEmptyColumns(Arg... args) {
		return verb(Mlr.Verbs.removeEmptyColumns(args));
	}

	/** Appends Miller verb <code>rename</code>; delegates to {@link Mlr.Verbs#rename}. */
	public Mlr rename(Arg... args) {
		return verb(Mlr.Verbs.rename(args));
	}

	/** Appends Miller verb <code>reorder</code>; delegates to {@link Mlr.Verbs#reorder}. */
	public Mlr reorder(Arg... args) {
		return verb(Mlr.Verbs.reorder(args));
	}

	/** Appends Miller verb <code>repeat</code>; delegates to {@link Mlr.Verbs#repeat}. */
	public Mlr repeat(Arg... args) {
		return verb(Mlr.Verbs.repeat(args));
	}

	/** Appends Miller verb <code>reshape</code>; delegates to {@link Mlr.Verbs#reshape}. */
	public Mlr reshape(Arg... args) {
		return verb(Mlr.Verbs.reshape(args));
	}

	/** Appends Miller verb <code>sample</code>; delegates to {@link Mlr.Verbs#sample}. */
	public Mlr sample(Arg... args) {
		return verb(Mlr.Verbs.sample(args));
	}

	/** Appends Miller verb <code>sec2gmt</code>; delegates to {@link Mlr.Verbs#sec2Gmt}. */
	public Mlr sec2Gmt(Arg... args) {
		return verb(Mlr.Verbs.sec2Gmt(args));
	}

	/** Appends Miller verb <code>sec2gmtdate</code>; delegates to {@link Mlr.Verbs#sec2Gmtdate}. */
	public Mlr sec2Gmtdate(Arg... args) {
		return verb(Mlr.Verbs.sec2Gmtdate(args));
	}

	/** Appends Miller verb <code>seqgen</code>; delegates to {@link Mlr.Verbs#seqgen}. */
	public Mlr seqgen(Arg... args) {
		return verb(Mlr.Verbs.seqgen(args));
	}

	/** Appends Miller verb <code>shuffle</code>; delegates to {@link Mlr.Verbs#shuffle}. */
	public Mlr shuffle(Arg... args) {
		return verb(Mlr.Verbs.shuffle(args));
	}

	/** Appends Miller verb <code>sparsify</code>; delegates to {@link Mlr.Verbs#sparsify}. */
	public Mlr sparsify(Arg... args) {
		return verb(Mlr.Verbs.sparsify(args));
	}

	/** Appends Miller verb <code>skipTrivialRecords</code>; delegates to {@link Mlr.Verbs#skipTrivialRecords}. */
	public Mlr skipTrivialRecords(Arg... args) {
		return verb(Mlr.Verbs.skipTrivialRecords(args));
	}

	/** Appends Miller verb <code>sort</code>; delegates to {@link Mlr.Verbs#sort}. */
	public Mlr sort(Arg... args) {
		return verb(Mlr.Verbs.sort(args));
	}

	/** Appends Miller verb <code>sortWithinRecords</code>; delegates to {@link Mlr.Verbs#sortWithinRecords}. */
	public Mlr sortWithinRecords(Arg... args) {
		return verb(Mlr.Verbs.sortWithinRecords(args));
	}

	/** Appends Miller verb <code>split</code>; delegates to {@link Mlr.Verbs#split}. */
	public Mlr splitVerb(Arg... args) {
		return verb(Mlr.Verbs.split(args));
	}

	/** Appends Miller verb <code>ssub</code>; delegates to {@link Mlr.Verbs#ssub}. */
	public Mlr ssub(Arg... args) {
		return verb(Mlr.Verbs.ssub(args));
	}

	/** Appends Miller verb <code>stats1</code>; delegates to {@link Mlr.Verbs#stats1}. */
	public Mlr stats1(Arg... args) {
		return verb(Mlr.Verbs.stats1(args));
	}

	/** Appends Miller verb <code>stats2</code>; delegates to {@link Mlr.Verbs#stats2}. */
	public Mlr stats2(Arg... args) {
		return verb(Mlr.Verbs.stats2(args));
	}

	/** Appends Miller verb <code>step</code>; delegates to {@link Mlr.Verbs#step}. */
	public Mlr step(Arg... args) {
		return verb(Mlr.Verbs.step(args));
	}

	/** Appends Miller verb <code>sub</code>; delegates to {@link Mlr.Verbs#sub}. */
	public Mlr sub(Arg... args) {
		return verb(Mlr.Verbs.sub(args));
	}

	/** Appends Miller verb <code>summary</code>; delegates to {@link Mlr.Verbs#summary}. */
	public Mlr summary(Arg... args) {
		return verb(Mlr.Verbs.summary(args));
	}

	/** Appends Miller verb <code>surv</code>; delegates to {@link Mlr.Verbs#surv}. */
	public Mlr surv(Arg... args) {
		return verb(Mlr.Verbs.surv(args));
	}

	/** Appends Miller verb <code>tac</code>; delegates to {@link Mlr.Verbs#tac}. */
	public Mlr tac(Arg... args) {
		return verb(Mlr.Verbs.tac(args));
	}

	/** Appends Miller verb <code>tail</code>; delegates to {@link Mlr.Verbs#tail}. */
	public Mlr tail(Arg... args) {
		return verb(Mlr.Verbs.tail(args));
	}

	/** Appends Miller verb <code>tee</code>; delegates to {@link Mlr.Verbs#tee}. */
	public Mlr tee(Arg... args) {
		return verb(Mlr.Verbs.tee(args));
	}

	/** Appends Miller verb <code>template</code>; delegates to {@link Mlr.Verbs#template}. */
	public Mlr template(Arg... args) {
		return verb(Mlr.Verbs.template(args));
	}

	/** Appends Miller verb <code>top</code>; delegates to {@link Mlr.Verbs#top}. */
	public Mlr top(Arg... args) {
		return verb(Mlr.Verbs.top(args));
	}

	/** Appends Miller verb <code>unflatten</code>; delegates to {@link Mlr.Verbs#unflatten}. */
	public Mlr unflatten(Arg... args) {
		return verb(Mlr.Verbs.unflatten(args));
	}

	/** Appends Miller verb <code>uniq</code>; delegates to {@link Mlr.Verbs#uniq}. */
	public Mlr uniq(Arg... args) {
		return verb(Mlr.Verbs.uniq(args));
	}

	/** Appends Miller verb <code>unspace</code>; delegates to {@link Mlr.Verbs#unspace}. */
	public Mlr unspace(Arg... args) {
		return verb(Mlr.Verbs.unspace(args));
	}

	/** Appends Miller verb <code>unsparsify</code>; delegates to {@link Mlr.Verbs#unsparsify}. */
	public Mlr unsparsify(Arg... args) {
		return verb(Mlr.Verbs.unsparsify(args));
	}

	/** Appends {@code head -n count}; shorthand for {@code head(HeadTail.n(count))}. */
	public Mlr head(int count) {
		return head(HeadTail.n(count));
	}

	/** Appends {@code tail -n count}; shorthand for {@code tail(HeadTail.n(count))}. */
	public Mlr tail(int count) {
		return tail(HeadTail.n(count));
	}

	/** Appends {@code cut -f fields} (field order follows input). */
	public Mlr cutFields(String fields) {
		return cut(Option.option(CutFlags.f(fields)));
	}

	/** Appends {@code cut -o -f fields} (reorder to match {@code fields}). */
	public Mlr cutOrdered(String fields) {
		return cut(Option.option(CutFlags.o()), Option.option(CutFlags.f(fields)));
	}

	/** Appends {@code cut -x -f fields} (omit listed fields). */
	public Mlr cutExcept(String fields) {
		return cut(Option.option(CutFlags.x()), Option.option(CutFlags.f(fields)));
	}

	/**
	 * Appends {@code stats1} with aggregations, numeric field, and optional group-by (comma-separated if multiple).
	 */
	public Mlr stats1(String aggregations, String field, String groupBy) {
		return stats1(
				StatsFlags.aggregations(aggregations),
				StatsFlags.field(field),
				StatsFlags.groupBy(groupBy));
	}

	/** Appends {@code stats1} without {@code -g}. */
	public Mlr stats1(String aggregations, String field) {
		return stats1(StatsFlags.aggregations(aggregations), StatsFlags.field(field));
	}

	/** Appends {@code split -g field}. */
	public Mlr splitBy(String field) {
		return splitVerb(SplitFlags.group(field));
	}

	/** Appends {@code put -q} then the expression (e.g. tee splits). */
	public Mlr putQuiet(Objective expression) {
		return put(PutFlags.quiet(), expression);
	}

	// --- Global format / IO flags (fluent; each appends one Miller global flag) ---

	public Mlr icsv() {
		return flag(Flags.icsv());
	}

	public Mlr ocsv() {
		return flag(Flags.ocsv());
	}

	public Mlr opprint() {
		return flag(Flags.opprint());
	}

	public Mlr ojson() {
		return flag(Flags.ojson());
	}

	public Mlr ijson() {
		return flag(Flags.ijson());
	}

	/**
	 * Appends Miller {@code --json} (input and output JSON). Named {@code jsonFlag} so IDE completion groups {@code json*}
	 * separately from verb helpers {@link #jsonParse} / {@link #jsonStringify}.
	 */
	public Mlr jsonFlag() {
		return flag(Flags.json());
	}

	public Mlr oxtab() {
		return flag(Flags.oxtab());
	}

	public Mlr ixtab() {
		return flag(Flags.ixtab());
	}

	public Mlr c2p() {
		return flag(Flags.c2p());
	}

	/** Appends {@code --c2x}. */
	public Mlr c2x() {
		return flag(Flags.c2x());
	}

	/** Appends {@code --dkvp} (input and output DKVP). */
	public Mlr dkvp() {
		return flag(Flags.dkvp());
	}

	/** Appends {@code --odkvp}. */
	public Mlr odkvp() {
		return flag(Flags.odkvp());
	}

	/** Appends {@code --ofs} with a Miller field-separator token (e.g. {@code pipe}, {@code tab}). */
	public Mlr ofs(String value) {
		return flag(Flags.ofs(value));
	}

	/** Appends {@code --ifs} with a Miller field-separator token (e.g. {@code comma}, {@code semicolon}). */
	public Mlr ifs(String value) {
		return flag(Flags.ifs(value));
	}

	/** Appends {@code --fs} with a Miller field-separator token. */
	public Mlr fs(String value) {
		return flag(Flags.fs(value));
	}

	/** Appends {@code --implicit-csv-header}. */
	public Mlr implicitCsvHeader() {
		return flag(Flags.implicitCsvHeader());
	}

	/** Appends Miller {@code --hi} (implicit CSV header shorthand). */
	public Mlr hi() {
		return flag(Flags.hi());
	}

	/** Appends {@code --inidx}. */
	public Mlr inidx() {
		return flag(Flags.inidx());
	}

	/** Appends {@code --nidx}. */
	public Mlr nidx() {
		return flag(Flags.nidx());
	}

	/** Appends {@code --headerless-csv-output}. */
	public Mlr headerlessCsvOutput() {
		return flag(Flags.headerlessCsvOutput());
	}

	/** Appends {@code --icsvlite}. */
	public Mlr icsvlite() {
		return flag(Flags.icsvlite());
	}

	/** Appends {@code --itsv}. */
	public Mlr itsv() {
		return flag(Flags.itsv());
	}

	/** Appends {@code --ipprint}. */
	public Mlr ipprint() {
		return flag(Flags.ipprint());
	}

	/** Appends {@code --seed} with a reproducible RNG seed (e.g. for {@code sample}). */
	public Mlr seed(String value) {
		return flag(Flags.seed(value));
	}

	/** Appends Miller {@code -n} (no input records; DSL-only programs). */
	public Mlr noInput() {
		return flag(Flags.noInput());
	}

	/**
	 * Appends {@code --csv} when chaining after {@link #inDir(String)} (named {@code csvFlag} so IDE completion lists it
	 * with other {@code csv*} helpers; static entry with CSV preset remains {@link #csv()}).
	 */
	public Mlr csvFlag() {
		return flag(Flags.csv());
	}

	/** Appends {@code --idkvp}. */
	public Mlr idkvp() {
		return flag(Flags.idkvp());
	}

	/** Appends {@code --tsv}. */
	public Mlr tsv() {
		return flag(Flags.tsv());
	}

	public Mlr from(String path) {
		return flag(Flags.from(path));
	}

	public Mlr inPlace() {
		return flag(Flags.inPlaceShort());
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	@Override
	public String toString() {
		if(mlrPath == null) {
			throw new IllegalArgumentException("mlrPath must not be null");
		}

		if(workingPath == null) {
			throw new IllegalArgumentException("workingPath must not be null");
		}

		StringBuilder toStrResult = new StringBuilder();
		toStrResult.append(mlrPath);
		for(Flag flag : flags) {
			toStrResult.append(SPACER);
			toStrResult.append(flag);
		}

		for (String token : preVerbArgs) {
			toStrResult.append(SPACER);
			toStrResult.append(token);
		}

		for(int i = 0 ; i < verbs.size() ; i++) {
			Verb verb = verbs.get(i);
			if (i > 0) {
				toStrResult.append(SPACER + CHAINING_ADVERB);
			}
			toStrResult.append(SPACER);
			toStrResult.append(verb);
		}

		for(String fileName : fileNames) {
			toStrResult.append(SPACER);
			toStrResult.append(fileName);
		}

		return toStrResult.toString();
	}

	/**
	 * Run mlr with data read from {@code isr} as standard input. Omit input files so Miller reads stdin.
	 * <p>
	 * Unlike {@link #run()}, this method returns nothing: standard output is only written when
	 * {@link #redirectOutputFile(File)} is set. Otherwise stdout is discarded so the process does not block on a full pipe.
	 * </p>
	 * <p>
	 * Input is copied line by line ({@link BufferedReader#readLine()}); platform line endings are not preserved and each
	 * record is terminated with a Unix newline ({@code \n}) on the child stdin.
	 * </p>
	 *
	 * @param isr character source for stdin; must not be null
	 * @throws IOException if piping stdin to mlr or draining stdout fails
	 */
	public void run(InputStreamReader isr) throws IOException, InterruptedException {
		if (isr == null) {
			throw new IllegalArgumentException("isr must not be null");
		}
		if (workingPath == null) {
			throw new IllegalArgumentException("workingPath must not be null");
		}

		List<String> executableAndArgs = new ArrayList<>();
		executableAndArgs.add(mlrPath);
		executableAndArgs.addAll(getArgs());
		processBuilder.directory(new File(workingPath));
		processBuilder.command(executableAndArgs);

		if (redirectOutputFile != null) {
			boolean created = redirectOutputFile.createNewFile();
			logger.info("redirectOutputFile created=" + created);
			processBuilder.redirectOutput(redirectOutputFile);
		}

		Process process = processBuilder.start();
		logger.info("start process=" + process.pid());

		AtomicReference<IOException> stdinFailure = new AtomicReference<>();
		AtomicReference<IOException> stdoutFailure = new AtomicReference<>();

		Thread stdinWriter = new Thread(() -> {
			try (BufferedReader br = new BufferedReader(isr);
					BufferedWriter bw = new BufferedWriter(
							new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = br.readLine()) != null) {
					bw.write(line);
					bw.newLine();
				}
			} catch (IOException e) {
				stdinFailure.set(e);
				process.destroy();
			}
		}, "mlr-stdin");
		stdinWriter.start();

		Thread stdoutDrainer = null;
		if (redirectOutputFile == null) {
			stdoutDrainer = new Thread(() -> {
				try {
					process.getInputStream().transferTo(OutputStream.nullOutputStream());
				} catch (IOException e) {
					stdoutFailure.set(e);
					process.destroy();
				}
			}, "mlr-stdout-drain");
			stdoutDrainer.start();
		}

		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
			process.destroyForcibly();
			Thread.currentThread().interrupt();
			throw e;
		}
		logger.info("exitCode=" + exitCode);

		joinOrDestroyOnInterrupt(stdinWriter, process);
		if (stdoutDrainer != null) {
			joinOrDestroyOnInterrupt(stdoutDrainer, process);
		}

		IOException stdinEx = stdinFailure.get();
		if (stdinEx != null) {
			throw new IOException("failed to pipe stdin to mlr", stdinEx);
		}
		IOException stdoutEx = stdoutFailure.get();
		if (stdoutEx != null) {
			throw new IOException("failed to drain mlr stdout", stdoutEx);
		}

		if (exitCode > 1) {
			stdErr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();
			logger.info("stdErr=" + stdErr);
			throw new RuntimeException("failed. exitCode=" + exitCode + " err=" + stdErr);
		}
	}

	private static void joinOrDestroyOnInterrupt(Thread thread, Process process) throws InterruptedException {
		try {
			thread.join();
		} catch (InterruptedException e) {
			process.destroyForcibly();
			Thread.currentThread().interrupt();
			throw e;
		}
	}

	/**
	 * execute mlr then return result
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String run() throws IOException, InterruptedException {
		String stdOut = null;
		List<String> executableAndArgs = new ArrayList<>();
		executableAndArgs.add(mlrPath);
		executableAndArgs.addAll(getArgs());
		//logger.info("executable and args=" + executableAndArgs);
		processBuilder.directory(new File(workingPath));
		processBuilder.command(executableAndArgs);
		//logger.info("processBuilder=" + processBuilder);

		if(redirectOutputFile != null) {
			boolean created = redirectOutputFile.createNewFile();
			logger.info("redirectOutputFile created=" + created);
			processBuilder.redirectOutput(redirectOutputFile);
		}
		Process process = processBuilder.start();
		logger.info("start process=" + process.pid());
		exitCode = process.waitFor();
		logger.info("exitCode=" + exitCode);
		if(exitCode > 1) {
			stdErr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8).trim();
			logger.info("stdErr=" + stdErr);
			throw new RuntimeException("failed. exitCode=" + exitCode + " err=" + stdErr);
		} else {
			stdOut = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
		}

		logger.info("stdOut=" + stdOut);
		return stdOut;
	}

	/**
	 * return args as string list
	 * @return
	 */
	private List<String> getArgs() {
		List<String> argList = new ArrayList<>();
		flags.stream().forEach(f -> argList.addAll(f.toStringList()));
		argList.addAll(preVerbArgs);
		for (int i = 0; i < verbs.size(); i++) {
			if (i > 0) {
				argList.add(CHAINING_ADVERB);
			}
			argList.addAll(verbs.get(i).toStringList());
		}
		argList.addAll(fileNames);
		return argList;
	}

	/**
	 * Static factories for every Miller verb; delegates to {@link net.shed.mlrbinder.verb.Verbs}.
	 * <p>
	 * Prefer {@code Mlr.cat()} on a chain; use static imports with {@code .verb(cat())} only when needed.
	 * </p>
	 */
	public static final class Verbs {
		private Verbs() {
		}

		public static Verb altkv(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.altkv(args);
		}

		public static Verb bar(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.bar(args);
		}

		public static Verb bootstrap(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.bootstrap(args);
		}

		public static Verb caseVerb(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.caseVerb(args);
		}

		public static Verb cat(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.cat(args);
		}

		public static Verb check(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.check(args);
		}

		public static Verb cleanWhitespace(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.cleanWhitespace(args);
		}

		public static Verb count(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.count(args);
		}

		public static Verb countDistinct(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.countDistinct(args);
		}

		public static Verb countSimilar(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.countSimilar(args);
		}

		public static Verb cut(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.cut(args);
		}

		public static Verb decimate(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.decimate(args);
		}

		public static Verb fillDown(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.fillDown(args);
		}

		public static Verb fillEmpty(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.fillEmpty(args);
		}

		public static Verb filter(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.filter(args);
		}

		public static Verb flatten(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.flatten(args);
		}

		public static Verb formatValues(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.formatValues(args);
		}

		public static Verb fraction(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.fraction(args);
		}

		public static Verb gap(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.gap(args);
		}

		public static Verb grep(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.grep(args);
		}

		public static Verb gsub(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.gsub(args);
		}

		public static Verb groupBy(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.groupBy(args);
		}

		public static Verb groupLike(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.groupLike(args);
		}

		public static Verb havingFields(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.havingFields(args);
		}

		public static Verb head(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.head(args);
		}

		public static Verb histogram(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.histogram(args);
		}

		public static Verb join(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.join(args);
		}

		public static Verb jsonParse(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.jsonParse(args);
		}

		public static Verb jsonStringify(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.jsonStringify(args);
		}

		public static Verb label(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.label(args);
		}

		public static Verb latin1ToUtf8(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.latin1ToUtf8(args);
		}

		public static Verb utf8ToLatin1(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.utf8ToLatin1(args);
		}

		public static Verb leastFrequent(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.leastFrequent(args);
		}

		public static Verb mergeFields(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.mergeFields(args);
		}

		public static Verb mostFrequent(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.mostFrequent(args);
		}

		public static Verb nest(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.nest(args);
		}

		public static Verb nothing(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.nothing(args);
		}

		public static Verb put(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.put(args);
		}

		public static Verb regularize(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.regularize(args);
		}

		public static Verb removeEmptyColumns(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.removeEmptyColumns(args);
		}

		public static Verb rename(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.rename(args);
		}

		public static Verb reorder(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.reorder(args);
		}

		public static Verb repeat(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.repeat(args);
		}

		public static Verb reshape(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.reshape(args);
		}

		public static Verb sample(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.sample(args);
		}

		public static Verb sec2Gmt(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.sec2Gmt(args);
		}

		public static Verb sec2Gmtdate(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.sec2Gmtdate(args);
		}

		public static Verb seqgen(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.seqgen(args);
		}

		public static Verb shuffle(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.shuffle(args);
		}

		public static Verb sparsify(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.sparsify(args);
		}

		public static Verb skipTrivialRecords(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.skipTrivialRecords(args);
		}

		public static Verb sort(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.sort(args);
		}

		public static Verb sortWithinRecords(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.sortWithinRecords(args);
		}

		public static Verb split(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.split(args);
		}

		public static Verb ssub(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.ssub(args);
		}

		public static Verb stats1(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.stats1(args);
		}

		public static Verb stats2(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.stats2(args);
		}

		public static Verb step(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.step(args);
		}

		public static Verb sub(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.sub(args);
		}

		public static Verb summary(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.summary(args);
		}

		public static Verb surv(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.surv(args);
		}

		public static Verb tac(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.tac(args);
		}

		public static Verb tail(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.tail(args);
		}

		public static Verb tee(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.tee(args);
		}

		public static Verb template(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.template(args);
		}

		public static Verb top(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.top(args);
		}

		public static Verb unflatten(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.unflatten(args);
		}

		public static Verb uniq(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.uniq(args);
		}

		public static Verb unspace(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.unspace(args);
		}

		public static Verb unsparsify(Arg... args) {
			return net.shed.mlrbinder.verb.Verbs.unsparsify(args);
		}
	}
}
