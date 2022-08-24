package mlrbinder.verb;

import java.util.ArrayList;
import java.util.List;

import mlrbinder.Flag;
import mlrbinder.MlrBinder;

public class Option {
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
