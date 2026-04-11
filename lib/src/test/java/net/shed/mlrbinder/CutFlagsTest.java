package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CutFlagsTest {

	@Test
	void oAndXAndF() {
		assertEquals("-o", CutFlags.o().toString());
		assertEquals("-x", CutFlags.x().toString());
		assertEquals("-f a,b", CutFlags.f("a,b").toString());
	}
}
