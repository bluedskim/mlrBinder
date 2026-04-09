package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FlagsTest {

	@Test
	void csvMatchesFlagCsv() {
		assertEquals(Flag.csv().toString(), Flags.csv().toString());
	}
}
