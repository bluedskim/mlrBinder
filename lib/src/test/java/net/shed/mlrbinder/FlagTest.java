package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FlagTest {
	@Test
	@DisplayName("constructor without object test")
	public void flagWithoutObject() {
		String flagName = "--flag";
		Flag flag = new Flag(flagName);

		assertEquals(flag.toString(), flagName);
	}

	@Test
	@DisplayName("constructor with object test")
	public void flagWithObject() {
		String flagName = "--flag";
		Flag flag = new Flag(flagName);

		String objectName = "objectName";
		Object obj = new Object(objectName);

		Flag flagWithObject = flag.object(obj);
		assertSame(flag, flagWithObject);
		assertEquals(flag.toString(), flagName + MlrBinder.SPACER + objectName);
	}

	@Test
	@DisplayName("toStringList test")
	public void toStringListTest() {
		List<String> stringList = new ArrayList<>();
		String flagName = "--flag";
		Flag flag = new Flag(flagName);
		stringList.add(flagName);

		String objectName = "objectName";
		Object obj = new Object(objectName);
		stringList.add(objectName);
		flag.object(obj);
		assertEquals(stringList, flag.toStringList());
	}
}
