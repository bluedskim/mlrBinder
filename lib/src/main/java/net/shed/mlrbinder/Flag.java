package net.shed.mlrbinder;

import java.util.ArrayList;
import java.util.List;

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

	public Object getObject() {
		return obj;
	}

	/**
	 * to command line executable arguments list
	 * @return
	 */
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
		return flagName + (obj != null ? MlrBinder.SPACER + obj : "");
	}

}
