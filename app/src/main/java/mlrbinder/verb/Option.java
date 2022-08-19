package mlrbinder.verb;

import mlrbinder.Flag;

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

	@Override
	public String toString() {
		return flag + (obj != null ? " " + obj : "");
	}
}
