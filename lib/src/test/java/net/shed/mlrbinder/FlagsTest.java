package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FlagsTest {

	@Test
	void csvMatchesFlagCsv() {
		assertEquals(Flag.csv().toString(), Flags.csv().toString());
	}

	@Test
	void rawRoundTrip() {
		assertEquals("--custom", Flags.raw("--custom").toString());
		assertEquals("--foo bar", Flags.raw("--foo", "bar").toString());
	}

	@Test
	void genFieldNameTakesValue() {
		assertEquals("--gen-field-name i", Flags.genFieldName("i").toString());
	}
}
