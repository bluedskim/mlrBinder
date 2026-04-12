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
import net.shed.mlrbinder.verb.Option;
import net.shed.mlrbinder.verb.Verb;

/**
 * Miller command builder and runner: executable path + global {@link Flag}s + optional {@code --mfrom}/{@code --mload}
 * tokens + chained {@link net.shed.mlrbinder.verb.Verb}s + input files, then {@link #run()} or {@link #run(InputStreamReader)}.
 * <p>
 * Prefer static helpers {@link #inDir(String)}, {@link #csv()}, {@link #mlr()} over raw constructors when possible.
 * Build verbs with {@link Verbs} ({@code import static …Mlr.Verbs.*}).
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

	/**
	 * Appends a {@code sort} verb (same as {@code .verb(Mlr.Verbs.sort(args))}).
	 */
	public Mlr sort(Arg... args) {
		return verb(Mlr.Verbs.sort(args));
	}

	/** Appends {@code cat} with optional extra Miller arguments. */
	public Mlr cat(Arg... args) {
		return verb(Mlr.Verbs.cat(args));
	}

	/** Appends {@code head -n count}. */
	public Mlr head(int count) {
		return verb(Mlr.Verbs.head(HeadTail.n(count)));
	}

	/** Appends {@code tail -n count}. */
	public Mlr tail(int count) {
		return verb(Mlr.Verbs.tail(HeadTail.n(count)));
	}

	/** Appends {@code filter} with a Miller DSL expression. */
	public Mlr filter(Objective expression) {
		return verb(Mlr.Verbs.filter(expression));
	}

	/** Appends {@code put} with a Miller DSL expression (or script body). */
	public Mlr put(Objective expression) {
		return verb(Mlr.Verbs.put(expression));
	}

	/** Appends {@code put -q} then the expression (e.g. tee splits). */
	public Mlr putQuiet(Objective expression) {
		return verb(Mlr.Verbs.put(PutFlags.quiet(), expression));
	}

	/** Appends {@code cut -f fields} (field order follows input). */
	public Mlr cutFields(String fields) {
		return verb(Mlr.Verbs.cut(Option.option(CutFlags.f(fields))));
	}

	/** Appends {@code cut -o -f fields} (reorder to match {@code fields}). */
	public Mlr cutOrdered(String fields) {
		return verb(Mlr.Verbs.cut(Option.option(CutFlags.o()), Option.option(CutFlags.f(fields))));
	}

	/** Appends {@code cut -x -f fields} (omit listed fields). */
	public Mlr cutExcept(String fields) {
		return verb(Mlr.Verbs.cut(Option.option(CutFlags.x()), Option.option(CutFlags.f(fields))));
	}

	/**
	 * Appends {@code stats1} with aggregations, numeric field, and optional group-by (comma-separated if multiple).
	 */
	public Mlr stats1(String aggregations, String field, String groupBy) {
		return verb(Mlr.Verbs.stats1(
				StatsFlags.aggregations(aggregations),
				StatsFlags.field(field),
				StatsFlags.groupBy(groupBy)));
	}

	/** Appends {@code stats1} without {@code -g}. */
	public Mlr stats1(String aggregations, String field) {
		return verb(Mlr.Verbs.stats1(StatsFlags.aggregations(aggregations), StatsFlags.field(field)));
	}

	/** Appends {@code split -g field}. */
	public Mlr splitBy(String field) {
		return verb(Mlr.Verbs.split(SplitFlags.group(field)));
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

	public Mlr json() {
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
	 * Recommended: {@code import static net.shed.mlrbinder.Mlr.Verbs.*;} then {@code .verb(cat())}.
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
