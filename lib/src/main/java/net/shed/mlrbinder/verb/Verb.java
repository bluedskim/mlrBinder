package net.shed.mlrbinder.verb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.shed.mlrbinder.Arg;
import net.shed.mlrbinder.Mlr;

/**
 * Miller verb segment for the command line. Prefer {@link net.shed.mlrbinder.Mlr.Verbs} to construct verbs.
 */
public class Verb implements Arg {
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

	public Verb(String verbName, Arg... args) {
		this(verbName);
		this.args = Arrays.asList(args);
	}	

	@Override
	public String toString() {
		return verbName + toStringArgs();
	}

	private String toStringArgs() {
		StringBuilder concatenatedOptions = new StringBuilder();
		for(Arg arg : args) {
			concatenatedOptions.append(Mlr.SPACER + arg);
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
