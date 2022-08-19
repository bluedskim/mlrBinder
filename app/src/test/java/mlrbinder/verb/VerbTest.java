package mlrbinder.verb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VerbTest {

	@Test
	public void verbWithoutOptionsTest() {
		String verbName = "verbName";
		Verb verb = new Verb(verbName);
		assertEquals(verb.toString(), verbName);
	}

	@Test
	public void verbWithOneOptionTest() {
		assertEquals("null", "");
	}

	@Test
	public void verbWithOptionsTest() {
		assertEquals("null", "");
	}
}
