package net.shed.mlrbinder;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import net.shed.mlrbinder.verb.Verb;

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
	 * return list of verbs
	 * @return verbs
	 */
	public List<Verb> getVerbs() {
		return verbs;
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
	 * execute mlr then connect output stream to isr
	 * @param isr
	 */
	public void run(InputStreamReader isr) {

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
			logger.info("redirectInputFile created=" + created);
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
		verbs.stream().forEach(v -> argList.addAll(v.toStringList()));
		argList.addAll(fileNames);
		return argList;
	}
}
