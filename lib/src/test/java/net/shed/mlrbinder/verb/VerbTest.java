package net.shed.mlrbinder.verb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.shed.mlrbinder.Flag;
import net.shed.mlrbinder.Mlr;
import net.shed.mlrbinder.verb.Verbs;

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

	@Test
	public void staticSortMethodTest() {
		Verb verb = Mlr.Verbs.sort();
		assertEquals("sort", verb.toString());
	}

	@Test
	public void mlrVerbsMatchesCanonicalVerbs() {
		assertEquals(Verbs.sort().toString(), Mlr.Verbs.sort().toString());
		assertEquals(Verbs.cat(new Flag("-n")).toString(), Mlr.Verbs.cat(new Flag("-n")).toString());
		assertEquals(Verbs.caseVerb().toString(), Mlr.Verbs.caseVerb().toString());
		assertEquals(Verbs.gsub().toString(), Mlr.Verbs.gsub().toString());
		assertEquals(Verbs.sparsify().toString(), Mlr.Verbs.sparsify().toString());
		assertEquals(Verbs.ssub().toString(), Mlr.Verbs.ssub().toString());
		assertEquals(Verbs.sub().toString(), Mlr.Verbs.sub().toString());
		assertEquals(Verbs.surv().toString(), Mlr.Verbs.surv().toString());
		assertEquals(Verbs.unspace().toString(), Mlr.Verbs.unspace().toString());
	}
}
