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
import net.shed.mlrbinder.verb.Verb;
import net.shed.mlrbinder.verb.Verbs;

/**
 * Mlr(Miller) binder
 * [Structure]
 * The program name + Flags + Verbs + Zero or more filenames
 */
public class MlrBinder {
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

	private static Logger logger = Logger.getLogger(MlrBinder.class.getName());

	Integer exitCode;
	String stdErr;
	private String mlrPath;

	/**
	 * default constructor
	 */
	public MlrBinder() {
		super();
		mlrPath = DEFAULT_MLR_PATH;
		processBuilder = new ProcessBuilder();
	}

	/**
	 * constructor, get ProcessBuilder
	 * @param mlrPath
	 */
	public MlrBinder(ProcessBuilder processBuilder) {
		this();
		this.processBuilder = processBuilder;
	}

	/**
	 * constructor, get mlr executable path
	 * @param mlrPath
	 */
	public MlrBinder(String mlrPath) {
		this();
		this.mlrPath = mlrPath;
	}

	/**
	 * constructor, get mlr executable path
	 * @param mlrPath
	 */
	public MlrBinder(String mlrPath, String workingPath) {
		this();
		this.mlrPath = mlrPath;
		this.workingPath = workingPath;
	}

	/**
	 * set executable path then return self
	 * @param mlrPath
	 * @return
	 */
	public MlrBinder mlrPath(String mlrPath) {
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
	public MlrBinder workingPath(String workingPath) {
		this.workingPath = workingPath;
		return this;
	}

	private File redirectOutputFile;

	public MlrBinder redirectOutputFile(File redirectOutputFile) {
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
	 * @return this MlrBinder
	 */
	public MlrBinder flag(Flag flag) {
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
	public MlrBinder mfrom(String... paths) {
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
	public MlrBinder mload(String... scripts) {
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
	 * add verbs and return this MlrBinder
	 * @param flag
	 * @return this MlrBinder
	 */
	public MlrBinder verb(Verb... verbs) {
		this.verbs.addAll(Arrays.asList(verbs));
		return this;
	}

	/**
	 * list of file names
	 */
	private List<String> fileNames = new ArrayList<>();

	public MlrBinder file(String fileName) {
		fileNames.add(fileName);
		return this;
	}

	/**
	 * Adds an input file argument. If {@link #workingPath} is not set yet: for a relative file, defaults the process
	 * working directory to {@code user.dir}; for an absolute file, sets it to the file's parent directory (file name
	 * only is passed to {@code mlr}, matching {@link #file(String)} with a bare name and matching working directory).
	 */
	public MlrBinder file(File file) {
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
	public static MlrBinder csv() {
		return new MlrBinder().flag(Flags.csv());
	}

	/**
	 * Alias for {@link #workingPath(String)} for fluent chains.
	 */
	public MlrBinder workDir(String path) {
		return workingPath(path);
	}

	/**
	 * Sets the process working directory from {@code dir}'s path.
	 */
	public MlrBinder workDir(File dir) {
		Objects.requireNonNull(dir, "dir");
		return workingPath(dir.getPath());
	}

	/**
	 * Appends a {@code sort} verb with the given Miller arguments (e.g. from {@link SortFlags#n} / {@link SortFlags#nr}).
	 */
	public MlrBinder sort(Arg... args) {
		return verb(Verbs.sort(args));
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
		verbs.stream().forEach(v -> argList.addAll(v.toStringList()));
		argList.addAll(fileNames);
		return argList;
	}
}
