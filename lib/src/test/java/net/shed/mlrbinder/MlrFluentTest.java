package net.shed.mlrbinder;

import static net.shed.mlrbinder.Flags.csv;
import static net.shed.mlrbinder.Flags.from;
import static net.shed.mlrbinder.Flags.icsv;
import static net.shed.mlrbinder.Flags.opprint;
import static net.shed.mlrbinder.Mlr.Verbs.cut;
import static net.shed.mlrbinder.Mlr.Verbs.head;
import static net.shed.mlrbinder.Mlr.Verbs.sort;
import static net.shed.mlrbinder.Mlr.Verbs.stats1;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Option.option;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * {@link Mlr} chains with {@link Mlr.Verbs} match expected argv.
 */
class MlrFluentTest {

	@Test
	void icsvOpprintHeadMatchesExpected() {
		String s = Mlr.inDir("wp")
				.icsv()
				.opprint()
				.verb(head(HeadTail.n(3)))
				.file("ex.csv")
				.toString();
		assertEquals("mlr --icsv --opprint head -n 3 ex.csv", s);
	}

	@Test
	void cutOrderedMatchesExpected() {
		String s = Mlr.inDir("wp")
				.icsv()
				.opprint()
				.verb(cut(option(CutFlags.o()), option(CutFlags.f("a,b"))))
				.file("ex.csv")
				.toString();
		assertEquals("mlr --icsv --opprint cut -o -f a,b ex.csv", s);
	}

	@Test
	void stats1WithGroupMatchesExpected() {
		String s = Mlr.inDir("wp")
				.from("ex.csv")
				.verb(stats1(
						StatsFlags.aggregations("count"),
						StatsFlags.field("qty"),
						StatsFlags.groupBy("shape")))
				.toString();
		assertEquals("mlr --from ex.csv stats1 -a count -f qty -g shape", s);
	}

	@Test
	void sortChainedWithHead() {
		String s = Mlr.inDir("wp")
				.flag(csv())
				.verb(sort(nr("index")))
				.verb(head(HeadTail.n(2)))
				.file("ex.csv")
				.toString();
		assertEquals("mlr --csv sort -nr index then head -n 2 ex.csv", s);
	}
}
