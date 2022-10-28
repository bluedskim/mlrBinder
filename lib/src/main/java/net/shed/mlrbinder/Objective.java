package net.shed.mlrbinder;

import java.util.Arrays;
import java.util.Collection;

/**
 * represents objective of all subjective like verb
 */
public class Objective implements Arg {
	private String name;

	public Objective(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Collection<String> toStringList() {
		return Arrays.asList(new String[]{name});
	}
}
