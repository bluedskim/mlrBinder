package net.shed.mlrbinder;

import java.util.ArrayList;
import java.util.List;

/**
 * mlr flag
 * reference : https://miller.readthedocs.io/en/latest/reference-main-flag-list/
 * eg. --from {filename}<br/>
 * --tz {timezone}<br/>
 */
public class Flag implements Arg {
	/**
	 * starts with dash or double dash
	 * --csv or -c
	 */
	private String flagName;

	/**
	 * Verb-local or ad-hoc flag token (e.g. {@code flag("-f").objective("shape")}).
	 * For global Miller flags prefer {@link Flags} factories or {@link Flags#raw(String)}.
	 */
	public static Flag flag(String name) {
		return new Flag(name);
	}

	public static Flag csv() {
		return Flags.csv();
	}

	public Flag(String flagName) {
		super();
		this.flagName = flagName;
	}

	private Objective obj;

	public Flag objective(Objective obj) {
		this.obj = obj;
		return this;
	}

	public Flag objective(String objStr) {
		this.obj = Objective.objective(objStr);
		return this;
	}

	public Objective getObjective() {
		return obj;
	}

	/**
	 * to command line executable arguments list
	 * @return
	 */
	@Override
	public List<String> toStringList() {
		List<String> stringList = new ArrayList<>();
		stringList.add(flagName);
		if(obj != null) {
			stringList.add(obj.toString());
		}
		return stringList;
	}

	@Override
	public String toString() {
		return flagName + (obj != null ? Mlr.SPACER + obj : "");
	}

}
