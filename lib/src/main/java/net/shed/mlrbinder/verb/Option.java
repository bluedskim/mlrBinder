package net.shed.mlrbinder.verb;

import java.util.ArrayList;
import java.util.List;

import net.shed.mlrbinder.Arg;
import net.shed.mlrbinder.Flag;
import net.shed.mlrbinder.MlrBinder;

public class Option implements Arg {
	private Flag flag;
	private Object obj;

	public Option(Flag flag) {
		this.flag = flag;
	}
	public Option(Flag flag, Object obj) {
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
