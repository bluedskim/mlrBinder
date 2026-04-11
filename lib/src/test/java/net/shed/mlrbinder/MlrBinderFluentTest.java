package net.shed.mlrbinder;

import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Option.option;
import static net.shed.mlrbinder.verb.Verbs.cut;
import static net.shed.mlrbinder.verb.Verbs.head;
import static net.shed.mlrbinder.verb.Verbs.stats1;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * {@link MlrBinder} fluent verb / global-flag chain helpers match hand-built argv.
 */
class MlrBinderFluentTest {

	@Test
	void icsvOpprintHeadMatchesManual() {
		String manual = new MlrBinder("mlr", "wp")
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(head(option(n(), objective("3"))))
				.file("ex.csv")
				.toString();
		String fluent = new MlrBinder("mlr", "wp")
				.icsv()
				.opprint()
				.head(3)
				.file("ex.csv")
				.toString();
		assertEquals(manual, fluent);
		assertEquals("mlr --icsv --opprint head -n 3 ex.csv", fluent);
	}

	@Test
	void cutOrderedMatchesManual() {
		String manual = new MlrBinder("mlr", "wp")
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(cut(option(CutFlags.o()), option(CutFlags.f("a,b"))))
				.file("ex.csv")
				.toString();
		String fluent = new MlrBinder("mlr", "wp")
				.icsv()
				.opprint()
				.cutOrdered("a,b")
				.file("ex.csv")
				.toString();
		assertEquals(manual, fluent);
	}

	@Test
	void stats1WithGroupMatchesManual() {
		String manual = new MlrBinder("mlr", "wp")
				.flag(Flags.from("ex.csv"))
				.verb(stats1(
						StatsFlags.aggregations("count"),
						StatsFlags.field("qty"),
						StatsFlags.groupBy("shape")))
				.toString();
		String fluent = new MlrBinder("mlr", "wp")
				.from("ex.csv")
				.stats1("count", "qty", "shape")
				.toString();
		assertEquals(manual, fluent);
	}

	@Test
	void sortChainedWithFluentHead() {
		String s = new MlrBinder("mlr", "wp")
				.flag(Flags.csv())
				.sort(nr("index"))
				.head(2)
				.file("ex.csv")
				.toString();
		assertEquals("mlr --csv sort -nr index then head -n 2 ex.csv", s);
	}
}
