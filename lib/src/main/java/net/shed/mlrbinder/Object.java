package net.shed.mlrbinder;

import java.util.Arrays;
import java.util.Collection;

/**
 * represents object of verb
 */
public class Object implements Arg {
	private String name;

	public Object(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Collection<? extends String> toStringList() {
		return Arrays.asList(new String[]{name});
	}
}
