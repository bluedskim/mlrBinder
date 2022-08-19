package mlrbinder.verb;

import mlrbinder.Flag;

public class Option {
	private Flag flag;
	private Object object;

	public Option(Flag flag) {
		this.flag = flag;
	}
	public Option(Flag flag, Object object) {
		this.flag = flag;
		this.object = object;
	}

	@Override
	public String toString() {
		return null;
	}
}
