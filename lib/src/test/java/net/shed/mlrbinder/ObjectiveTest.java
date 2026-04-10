package net.shed.mlrbinder;

import static net.shed.mlrbinder.Objective.objective;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ObjectiveTest {

	@Test
	@DisplayName("toString returns the name")
	void toStringReturnsName() {
		assertEquals("field", new Objective("field").toString());
	}

	@Test
	@DisplayName("toStringList is a single-element list")
	void toStringListSingleToken() {
		assertEquals(Arrays.asList("x"), new Objective("x").toStringList());
	}

	@Test
	@DisplayName("objective() matches constructor")
	void staticObjectiveFactory() {
		assertEquals(new Objective("a").toString(), objective("a").toString());
		assertEquals(new Objective("a").toStringList(), objective("a").toStringList());
	}
}
