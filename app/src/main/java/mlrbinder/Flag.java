package mlrbinder;

public class Flag {
	/**
	 * starts with dash or double dash
	 * --csv or -c
	 */
	private String flagName;
	public Flag(String flagName) {
		super();
		this.flagName = flagName;
	}

	private Object obj;

	public Flag object(Object obj) {
		this.obj = obj;
		return this;
	}

	@Override
	public String toString() {
		return flagName + " " + obj;
	}
}