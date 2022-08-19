package mlrbinder.verb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import mlrbinder.Flag;

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
	}

	@Test
	public void verbWithOptionsTest() {
		String verbName = "verbName";
		Verb verb = new Verb(verbName);
		Option option1 = new Option(new Flag("flagName1"));
		Option option2 = new Option(new Flag("flagName2"));
		verb.option(option1);
		verb.option(option2);
		assertEquals("verbName " + option1 + " " + option2, verb.toString());
	}

	@Test
	public void consecutiveVerbTest() {
		String verbName = "verbName";
		Verb verb = new Verb(verbName).isConsecutive(true);
		assertEquals("then " + verbName, verb.toString());
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
}
