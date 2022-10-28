package net.shed.mlrbinder.verb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.shed.mlrbinder.Flag;
import net.shed.mlrbinder.MlrBinder;
import net.shed.mlrbinder.Objective;

public class OptionTest {
	@Test
	@DisplayName("constructor with flag test")
	public void constructorWithFlagTest() {
		String flagName = "flagName";
		Flag flag = new Flag(flagName);
		Option optionWithFlag = new Option(flag);
		assertEquals(optionWithFlag.toString(), flagName.toString());
	}

	@Test
	@DisplayName("constructor with flag and object test")
	public void constructorWithFlagAndObjectTest() {
		String flagName = "flagName";
		Flag flag = new Flag(flagName);

		String objectName = "objectName";
		Objective obj = new Objective(objectName);

		Option optionWithFlag = new Option(flag, obj);
		assertEquals(optionWithFlag.toString(), flag.toString() + MlrBinder.SPACER + obj.toString());
	}

	@Test
	@DisplayName("toStringList test")
	public void toStringListTest() {
		List<String> stringList = new ArrayList<>();
		String flagName = "flagName";
		stringList.add(flagName);
		Flag flag = new Flag(flagName);
		Option optionWithFlag = new Option(flag);
		assertEquals(stringList, optionWithFlag.toStringList());

		stringList = new ArrayList<>();
		stringList.add(flagName);
		String objName = "objName";
		Objective obj = new Objective(objName);
		stringList.add(objName);
		Option optionWithFlagAndObject = new Option(flag, obj);
		assertEquals(stringList, optionWithFlagAndObject.toStringList());

	}
}
