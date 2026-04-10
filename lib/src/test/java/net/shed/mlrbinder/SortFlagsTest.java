package net.shed.mlrbinder;

import static net.shed.mlrbinder.SortFlags.f;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SortFlagsTest {

	@Test
	void nNoArgIsBareMinusN() {
		assertEquals(new Flag("-n").toString(), n().toString());
		assertEquals(new Flag("-n").toStringList(), n().toStringList());
	}

	@Test
	void nMatchesManualFlag() {
		assertEquals(new Flag("-n").objective("a").toString(), n("a").toString());
		assertEquals(new Flag("-n").objective("x").toStringList(), n("x").toStringList());
	}

	@Test
	void fMatchesManualFlag() {
		assertEquals(new Flag("-f").objective("shape").toString(), f("shape").toString());
		assertEquals(new Flag("-f").objective("x").toStringList(), f("x").toStringList());
	}

	@Test
	void nrMatchesManualFlag() {
		assertEquals(new Flag("-nr").objective("b").toString(), nr("b").toString());
		assertEquals(new Flag("-nr").objective("y").toStringList(), nr("y").toStringList());
	}
}
