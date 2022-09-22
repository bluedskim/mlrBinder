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
		verb.option(option);
		assertEquals("verbName " + option, verb.toString());

		List<String> stringList = new ArrayList<>();
		if(verb.isConsecutive()) {
			stringList.add(Verb.CHAINING_ADVERB);
		}
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
		Verb verb = new Verb(verbName).isConsecutive(isConsecutive);
		Option option1 = new Option(new Flag("flagName1"));
		Option option2 = new Option(new Flag("flagName2"));
		verb.option(option1).option(option2);

		List<String> stringList = new ArrayList<>();
		if(isConsecutive) {
			stringList.add(Verb.CHAINING_ADVERB);
		}
		stringList.add(verbName);
		stringList.addAll(option1.toStringList());
		stringList.addAll(option2.toStringList());
		assertEquals(stringList, verb.toStringList());
	}

	@Test
	public void consecutiveVerbTest() {
		String verbName = "verbName";
		Verb verb = new Verb(verbName).isConsecutive(true);
		assertEquals(Verb.CHAINING_ADVERB + " " + verbName, verb.toString());
	}

	@Test
	public void consecutiveVerbWithOptionTest() {
		String verbName = "verbName";
		Verb verb = new Verb(verbName).isConsecutive(true);
		Option option = new Option(new Flag("flagName"));
		verb.option(option);
		assertEquals("then " + verbName + " " + option, verb.toString());
	}

	@Test
	public void consecutiveVerbWithOptionsTest() {
		String verbName = "verbName";
		Verb verb = new Verb(verbName).isConsecutive(true);
		Option option1 = new Option(new Flag("flagName1"));
		Option option2 = new Option(new Flag("flagName2"));
		verb.option(option1);
		verb.option(option2);
		assertEquals("then " + verbName + " " + option1 + " " + option2, verb.toString());
	}

	@Test
	public void getOptionsTest() {
		List<Option> options = new ArrayList<>();
		Verb verb = new Verb(null);
		Option option1 = new Option(new Flag("flagName1"));
		options.add(option1);
		verb.option(option1);
		Option option2 = new Option(new Flag("flagName2"));
		options.add(option2);
		verb.option(option2);
		assertEquals(options, verb.getOptions());
	}
}
