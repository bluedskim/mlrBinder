package net.shed.mlrbinder.verb;

import java.util.ArrayList;
import java.util.List;

import net.shed.mlrbinder.Arg;
import net.shed.mlrbinder.MlrBinder;

public class Verb implements Arg {
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
	 * argument list
	 */
	private List<Arg> args = new ArrayList<>();

	/**
	 * return flag list
	 * @return
	 */
	public List<Arg> getOptions() {
		return args;
	}

	public Verb addArg(Arg arg) {
		args.add(arg);
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
		for(Arg option : args) {
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
		args.stream().forEach(o -> stringList.addAll(o.toStringList()));
		return stringList;
	}
}
