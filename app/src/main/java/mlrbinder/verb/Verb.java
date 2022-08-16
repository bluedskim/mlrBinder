package mlrbinder.verb;

import java.util.ArrayList;
import java.util.List;

public class Verb {
	/**
	 * verbs can be chained with CHAINING_ADVERB
	 */
	private static final String CHAINING_ADVERB = "then";

	String verb;

	/**
	 * is chaining verb
	 */
	boolean isConsecutive = false;

	/**
	 * flag list
	 */
	private List<Option> options = new ArrayList<>();

	/**
	 * return flag list
	 * @return
	 */
	public List<Option> getOptions() {
		return options;
	}


	public Verb(String verb) {
		super();
		this.verb = verb;
	}
}
