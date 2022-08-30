package mlrbinder.verb;

import java.util.ArrayList;
import java.util.List;

import mlrbinder.MlrBinder;

public class Verb {
	/**
	 * verbs can be chained with CHAINING_ADVERB
	 */
	public static final String CHAINING_ADVERB = "then";

	String verbName;

	/**
	 * is chained verb
	 */
	private boolean isConsecutive = false;

	public boolean isConsecutive() {
		return isConsecutive;
	}

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

	@Override
	public String toString() {
		return (isConsecutive ? CHAINING_ADVERB + MlrBinder.SPACER : "")
			+ verbName
			+ optionsToString();
	}

	private String optionsToString() {
		StringBuilder concatenatedOptions = new StringBuilder();
		for(Option option : options) {
			concatenatedOptions.append(MlrBinder.SPACER + option);
		}
		return concatenatedOptions.toString();
	}

	public Verb isConsecutive(boolean isConsecutive) {
		this.isConsecutive = isConsecutive;
		return this;
	}

	/**
	 * to command line executable arguments list
	 */
	public List<String> toStringList() {
		List<String> stringList = new ArrayList<>();
		if(isConsecutive) {
			stringList.add(CHAINING_ADVERB);
		}
		stringList.add(verbName);
		options.stream().forEach(o -> stringList.addAll(o.toStringList()));
		return stringList;
	}
}
