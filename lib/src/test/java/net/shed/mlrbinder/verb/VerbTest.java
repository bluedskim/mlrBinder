package net.shed.mlrbinder.verb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.shed.mlrbinder.Flag;

public class VerbTest {

	@Test
	public void verbWithoutOptionsTest() {
		String verbName = "verbName";
		Verb verb = new Verb(verbName);
		assertEquals(verb.toString(), verbName);
	}

	@Test
	public void verbWithOneOptionTest() {
		String verbName = "verbName";
		Verb verb = new Verb(verbName);
		Option option = new Option(new Flag("flagName"));
		verb.addArg(option);
		assertEquals("verbName " + option, verb.toString());

		List<String> stringList = new ArrayList<>();
		stringList.add(verbName);
		stringList.addAll(option.toStringList());
		assertEquals(stringList, verb.toStringList());
	}

	@Test
	public void verbWithOptionsTest() {
		verbWithOptionsTest(true);
		verbWithOptionsTest(false);
	}

	public void verbWithOptionsTest(boolean isConsecutive) {
		String verbName = "verbName";
		Verb verb = new Verb(verbName);
		Option option1 = new Option(new Flag("flagName1"));
		Option option2 = new Option(new Flag("flagName2"));
		verb.addArg(option1).addArg(option2);

		List<String> stringList = new ArrayList<>();
		stringList.add(verbName);
		stringList.addAll(option1.toStringList());
		stringList.addAll(option2.toStringList());
		assertEquals(stringList, verb.toStringList());
	}

	@Test
	public void getOptionsTest() {
		List<Option> options = new ArrayList<>();
		Verb verb = new Verb(null);
		Option option1 = new Option(new Flag("flagName1"));
		options.add(option1);
		verb.addArg(option1);
		Option option2 = new Option(new Flag("flagName2"));
		options.add(option2);
		verb.addArg(option2);
		assertEquals(options, verb.getArgs());
	}
}
