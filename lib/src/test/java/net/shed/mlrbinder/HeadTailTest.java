package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class HeadTailTest {

	@Test
	void headCountOptionArgv() {
		assertEquals(Arrays.asList("-n", "4"), HeadTail.n(4).toStringList());
	}
}
