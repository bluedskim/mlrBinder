package mlrbinder;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mlrbinder.verb.Verb;

/**
 * Mlr(Miller) binder
 * [Structure]
 * The program name + Flags + Verbs + Zero or more filenames
 */
public class MlrBinder {
	/**
	 * default constructor
	 */
	public MlrBinder() {
		super();
	}

	private String mlrPath;

	/**
	 * constructor, get mlr executable path
	 * @param mlrPath
	 */
	public MlrBinder(String mlrPath) {
		this();
		this.mlrPath = mlrPath;
	}

	/**
	 * set executable path then return self
	 * @param mlrPath
	 * @return
	 */
	public MlrBinder path(String mlrPath) {
		this.mlrPath = mlrPath;
		return this;
	}

	/**
	 * return mlr path
	 * @return
	 */
	public String getPath() {
		return mlrPath;
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
	 * @return
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
	 * @return
	 */
	public List<Verb> getVerbs() {
		return verbs;
	}

	/**
	 * add flag
	 * @param verb
	 * @return
	 */
	public MlrBinder verb(Verb verb) {
		verbs.add(verb);
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

	/**
	 * execute mlr then connect output stream to isr
	 * @param isr
	 */
	public void run(InputStreamReader isr) {

	}

	/**
	 * execute mlr then return result
	 * @return
	 */
	public String run() {
		return null;
	}
}
