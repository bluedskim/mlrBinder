package mlrbinder.verb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mlrbinder.Flag;
import mlrbinder.Object;

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
		Object obj = new Object(objectName);

		Option optionWithFlag = new Option(flag, obj);
		assertEquals(optionWithFlag.toString(), flag.toString() + " " + obj.toString());
	}
}
