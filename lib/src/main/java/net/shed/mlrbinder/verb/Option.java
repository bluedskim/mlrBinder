package net.shed.mlrbinder.verb;

import java.util.ArrayList;
import java.util.List;

import net.shed.mlrbinder.Arg;
import net.shed.mlrbinder.Flag;
import net.shed.mlrbinder.MlrBinder;
import net.shed.mlrbinder.Objective;

public class Option implements Arg {
	private Flag flag;
	private Objective obj;

	/** Same as {@code new Option(flag)}; use with {@code import static …Option.option}. */
	public static Option option(Flag flag) {
		return new Option(flag);
	}

	/** Same as {@code new Option(flag, obj)}. */
	public static Option option(Flag flag, Objective obj) {
		return new Option(flag, obj);
	}

	public Option(Flag flag) {
		this.flag = flag;
	}
	public Option(Flag flag, Objective obj) {
		this.flag = flag;
		this.obj = obj;
	}

	/**
	 * to command line executable arguments list
	 * @return
	 */
	@Override
	public List<String> toStringList() {
		List<String> stringList = new ArrayList<>();
		stringList.addAll(flag.toStringList());
		if(obj != null) {
			stringList.add(obj.toString());
		}
		return stringList;
	}

	@Override
	public String toString() {
		return flag + (obj != null ? MlrBinder.SPACER + obj : "");
	}
}
