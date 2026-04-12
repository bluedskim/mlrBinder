package net.shed.mlrbinder;

import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Option.option;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * {@link Mlr} recommended style: global-flag chain + verb-named instance methods.
 */
class MlrFluentTest {

	@Test
	void icsvOpprintHeadMatchesExpected() {
		String s = Mlr.inDir("wp")
				.icsv()
				.opprint()
				.head(3)
				.file("ex.csv")
				.toString();
		assertEquals("mlr --icsv --opprint head -n 3 ex.csv", s);
	}

	@Test
	void cutOrderedMatchesExpected() {
		String s = Mlr.inDir("wp")
				.icsv()
				.opprint()
				.cut(option(CutFlags.o()), option(CutFlags.f("a,b")))
				.file("ex.csv")
				.toString();
		assertEquals("mlr --icsv --opprint cut -o -f a,b ex.csv", s);
	}

	@Test
	void stats1WithGroupMatchesExpected() {
		String s = Mlr.inDir("wp")
				.from("ex.csv")
				.stats1(
						StatsFlags.aggregations("count"),
						StatsFlags.field("qty"),
						StatsFlags.groupBy("shape"))
				.toString();
		assertEquals("mlr --from ex.csv stats1 -a count -f qty -g shape", s);
	}

	@Test
	void sortChainedWithHead() {
		String s = Mlr.inDir("wp")
				.csvFlag()
				.sort(nr("index"))
				.head(2)
				.file("ex.csv")
				.toString();
		assertEquals("mlr --csv sort -nr index then head -n 2 ex.csv", s);
	}
}
