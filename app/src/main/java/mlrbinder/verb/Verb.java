package mlrbinder.verb;

import java.util.ArrayList;
import java.util.List;

public class Verb {
	/**
	 * verbs can be chained with CHAINING_ADVERB
	 */
	private static final String CHAINING_ADVERB = "then";

	String verbName;

	/**
	 * is chained verb
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

	public Verb option(Option addingOption) {
		options.add(addingOption);
		return this;
	}


	public Verb(String verbName) {
		super();
		this.verbName = verbName;
	}
}
