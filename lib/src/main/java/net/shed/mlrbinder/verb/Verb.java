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
	 * argument list
	 */
	private List<Arg> args = new ArrayList<>();

	/**
	 * return flag list
	 * @return
	 */
	public List<Arg> getArgs() {
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
		return verbName + toStringArgs();
	}

	private String toStringArgs() {
		StringBuilder concatenatedOptions = new StringBuilder();
		for(Arg arg : args) {
			concatenatedOptions.append(MlrBinder.SPACER + arg);
		}
		return concatenatedOptions.toString();
	}

	/**
	 * to command line executable arguments list
	 */
	@Override
	public List<String> toStringList() {
		List<String> stringList = new ArrayList<>();
		stringList.add(verbName);
		args.stream().forEach(o -> stringList.addAll(o.toStringList()));
		return stringList;
	}
}
