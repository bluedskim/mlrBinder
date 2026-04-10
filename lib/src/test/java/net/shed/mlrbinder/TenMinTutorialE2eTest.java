package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.verb.Option.option;

import net.shed.mlrbinder.verb.Verbs;

/**
 * End-to-end checks that {@link MlrBinder} builds argv matching the Miller
 * <a href="https://miller.readthedocs.io/en/latest/10min/">10-minute tutorial</a>.
 * Golden files under {@code src/test/resources/10min/expected/} are produced with
 * {@code mlr} from the environment (e.g. {@code mlr 6.11}); regenerate if Miller output changes.
 */
class TenMinTutorialE2eTest {

	static boolean mlrOnPath() {
		try {
			Process p = new ProcessBuilder("mlr", "--version").redirectErrorStream(true).start();
			boolean finished = p.waitFor(10, TimeUnit.SECONDS);
			return finished && p.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private static Path tenMinRoot() throws URISyntaxException {
		var url = TenMinTutorialE2eTest.class.getClassLoader().getResource("10min/example.csv");
		if (url == null) {
			throw new IllegalStateException("missing test resource 10min/example.csv");
		}
		return Paths.get(url.toURI()).getParent();
	}

	private static String expected(String name) throws IOException, URISyntaxException {
		Path p = tenMinRoot().resolve("expected").resolve(name);
		return Files.readString(p).trim();
	}

	private static String run(MlrBinder binder) throws IOException, InterruptedException {
		return binder.run().trim();
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void catCsv() throws Exception {
		Path root = tenMinRoot();
		MlrBinder mlr = new MlrBinder("mlr", root.toString())
				.flag(Flags.csv())
				.verb(Verbs.cat())
				.file("example.csv");
		assertEquals(expected("cat_csv.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void catIcsvOpprint() throws Exception {
		Path root = tenMinRoot();
		MlrBinder mlr = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.cat())
				.file("example.csv");
		assertEquals(expected("cat_icsv_opprint.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void headAndTail() throws Exception {
		Path root = tenMinRoot();
		MlrBinder head = new MlrBinder("mlr", root.toString())
				.flag(Flags.csv())
				.verb(Verbs.head(option(n(), objective("4"))))
				.file("example.csv");
		assertEquals(expected("head_n4.txt"), run(head));

		MlrBinder tail = new MlrBinder("mlr", root.toString())
				.flag(Flags.csv())
				.verb(Verbs.tail(option(n(), objective("4"))))
				.file("example.csv");
		assertEquals(expected("tail_n4.txt"), run(tail));

		MlrBinder tailJson = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.ojson())
				.verb(Verbs.tail(option(n(), objective("2"))))
				.file("example.csv");
		assertEquals(expected("tail_n2_json.txt"), run(tailJson));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void sortAndCut() throws Exception {
		Path root = tenMinRoot();
		MlrBinder sortF = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.sort(new Flag("-f").objective("shape")))
				.file("example.csv");
		assertEquals(expected("sort_f_shape.txt"), run(sortF));

		MlrBinder sortFnr = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.sort(new Flag("-f").objective("shape"), new Flag("-nr").objective("index")))
				.file("example.csv");
		assertEquals(expected("sort_f_shape_nr_index.txt"), run(sortFnr));

		MlrBinder cutKeep = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.cut(option(new Flag("-f").objective("flag,shape"))))
				.file("example.csv");
		assertEquals(expected("cut_f.txt"), run(cutKeep));

		MlrBinder cutOrdered = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.cut(
						option(new Flag("-o")),
						option(new Flag("-f").objective("flag,shape"))))
				.file("example.csv");
		assertEquals(expected("cut_o_f.txt"), run(cutOrdered));

		MlrBinder cutOmit = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.cut(
						option(new Flag("-x")),
						option(new Flag("-f").objective("flag,shape"))))
				.file("example.csv");
		assertEquals(expected("cut_x_f.txt"), run(cutOmit));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void putPositionalFieldNamesAndValues() throws Exception {
		Path root = tenMinRoot();
		MlrBinder rename = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.put(objective("$[[3]] = \"NEW\"")))
				.file("example.csv");
		assertEquals(expected("put_rename_field3.txt"), run(rename));

		MlrBinder value = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.put(objective("$[[[3]]] = \"NEW\"")))
				.file("example.csv");
		assertEquals(expected("put_value_field3.txt"), run(value));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void filterAndPutComputed() throws Exception {
		Path root = tenMinRoot();
		MlrBinder f1 = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.filter(objective("$color == \"red\"")))
				.file("example.csv");
		assertEquals(expected("filter_color_red.txt"), run(f1));

		MlrBinder f2 = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.filter(objective("$color == \"red\" && $flag == \"true\"")))
				.file("example.csv");
		assertEquals(expected("filter_red_true.txt"), run(f2));

		String putExpr = "$ratio = $quantity / $rate; $color_shape = $color . \"_\" . $shape";
		MlrBinder put = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.put(objective(putExpr)))
				.file("example.csv");
		assertEquals(expected("put_ratio_color_shape.txt"), run(put));

		// Tutorial shows $y2 (typo); Miller uses ** for exponent — see tutorial prose / corrected samples.
		String yzExpr = "$y = $index + 1; $z = $y**2 + $k";
		MlrBinder fromPut = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.flag(Flags.from("example.csv"))
				.verb(Verbs.put(objective(yzExpr)));
		assertEquals(expected("put_y_z.txt"), run(fromPut));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void spacesFieldNames() throws Exception {
		Path root = tenMinRoot();
		MlrBinder sort = new MlrBinder("mlr", root.toString())
				.flag(Flags.c2p())
				.verb(Verbs.sort(new Flag("-nr").objective("Total MWh")))
				.file("spaces.csv");
		assertEquals(expected("spaces_sort.txt"), run(sort));

		MlrBinder put = new MlrBinder("mlr", root.toString())
				.flag(Flags.c2p())
				.verb(Verbs.put(objective("${Total KWh} = ${Total MWh} * 1000")))
				.file("spaces.csv");
		assertEquals(expected("spaces_put_kwh.txt"), run(put));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void multipleInputFiles() throws Exception {
		Path root = tenMinRoot();
		MlrBinder mlr = new MlrBinder("mlr", root.toString())
				.flag(Flags.csv())
				.verb(Verbs.cat())
				.file("data/a.csv")
				.file("data/b.csv");
		assertEquals(expected("multi_cat.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void thenChainingAndFrom() throws Exception {
		Path root = tenMinRoot();
		MlrBinder chained = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(
						Verbs.sort(new Flag("-nr").objective("index")),
						Verbs.head(option(n(), objective("3"))))
				.file("example.csv");
		assertEquals(expected("sort_then_head.txt"), run(chained));

		MlrBinder fromHead = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.flag(Flags.from("example.csv"))
				.verb(
						Verbs.sort(new Flag("-nr").objective("index")),
						Verbs.head(option(n(), objective("3"))));
		assertEquals(expected("from_sort_then_head.txt"), run(fromHead));

		MlrBinder fromChainCut = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.flag(Flags.from("example.csv"))
				.verb(
						Verbs.sort(new Flag("-nr").objective("index")),
						Verbs.head(option(n(), objective("3"))),
						Verbs.cut(option(new Flag("-f").objective("shape,quantity"))));
		assertEquals(expected("from_chain_cut.txt"), run(fromChainCut));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void headGroupByAndStats1() throws Exception {
		Path root = tenMinRoot();
		MlrBinder headG = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(
						Verbs.sort(new Flag("-f").objective("shape"), new Flag("-nr").objective("index")),
						Verbs.head(
								option(n(), objective("1")),
								option(new Flag("-g"), objective("shape"))))
				.file("example.csv");
		assertEquals(expected("head_g_shape.txt"), run(headG));

		MlrBinder statsShape = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.flag(Flags.from("example.csv"))
				.verb(Verbs.stats1(
						new Flag("-a").objective("count,min,mean,max"),
						new Flag("-f").objective("quantity"),
						new Flag("-g").objective("shape")));
		assertEquals(expected("stats1_g_shape.txt"), run(statsShape));

		MlrBinder statsShapeColor = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.flag(Flags.from("example.csv"))
				.verb(Verbs.stats1(
						new Flag("-a").objective("count,min,mean,max"),
						new Flag("-f").objective("quantity"),
						new Flag("-g").objective("shape,color")));
		assertEquals(expected("stats1_g_shape_color.txt"), run(statsShapeColor));

		MlrBinder statsXtab = new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.oxtab())
				.flag(Flags.from("example.csv"))
				.verb(Verbs.stats1(
						new Flag("-a").objective("p0,p10,p25,p50,p75,p90,p99,p100"),
						new Flag("-f").objective("rate")));
		assertEquals(expected("stats1_rate_percentiles_xtab.txt"), run(statsXtab));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void unicodeSamples() throws Exception {
		Path root = tenMinRoot();
		MlrBinder greekFilter = new MlrBinder("mlr", root.toString())
				.flag(Flags.c2p())
				.verb(Verbs.filter(objective("$σχήμα == \"κύκλος\"")))
				.file("παράδειγμα.csv");
		assertEquals(expected("greek_filter_circles.txt"), run(greekFilter));

		MlrBinder greekSort = new MlrBinder("mlr", root.toString())
				.flag(Flags.c2p())
				.verb(Verbs.sort(new Flag("-f").objective("σημαία")))
				.file("παράδειγμα.csv");
		assertEquals(expected("greek_sort_flag.txt"), run(greekSort));

		MlrBinder ruPut = new MlrBinder("mlr", root.toString())
				.flag(Flags.c2p())
				.verb(Verbs.put(objective("$форма = toupper($форма); $длина = strlen($цвет)")))
				.file("пример.csv");
		assertEquals(expected("russian_put_toupper_strlen.txt"), run(ruPut));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void jsonAndNestedConversion() throws Exception {
		Path root = tenMinRoot();
		MlrBinder jsonCat = new MlrBinder("mlr", root.toString())
				.flag(Flags.json())
				.verb(Verbs.cat())
				.file("example.json");
		assertEquals(expected("json_cat.txt"), run(jsonCat));

		MlrBinder jsonCsv = new MlrBinder("mlr", root.toString())
				.flag(Flags.ijson())
				.flag(Flags.ocsv())
				.verb(Verbs.cat())
				.file("example.json");
		assertEquals(expected("json_to_csv.txt"), run(jsonCsv));

		MlrBinder srvCsv = new MlrBinder("mlr", root.toString())
				.flag(Flags.ijson())
				.flag(Flags.ocsv())
				.verb(Verbs.cat())
				.file("data/server-log.json");
		assertEquals(expected("server_log_to_csv.txt"), run(srvCsv));

		MlrBinder srvXtab = new MlrBinder("mlr", root.toString())
				.flag(Flags.ijson())
				.flag(Flags.oxtab())
				.verb(Verbs.cat())
				.file("data/server-log.json");
		assertEquals(expected("server_log_to_xtab.txt"), run(srvXtab));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void jsonXtabRoundTrip() throws Exception {
		Path root = tenMinRoot();
		Path tmp = Files.createTempDirectory("mlr-10min-xtab");
		Path xtabFile = tmp.resolve("sl.xtab");
		new MlrBinder("mlr", root.toString())
				.flag(Flags.ijson())
				.flag(Flags.oxtab())
				.verb(Verbs.cat())
				.file("data/server-log.json")
				.redirectOutputFile(xtabFile.toFile())
				.run();

		MlrBinder back = new MlrBinder("mlr", tmp.toString())
				.flag(Flags.ixtab())
				.flag(Flags.ojson())
				.verb(Verbs.cat())
				.file(xtabFile.getFileName().toString());
		assertEquals(expected("json_xtab_roundtrip.txt"), run(back));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void redirectOutputToFile() throws Exception {
		Path root = tenMinRoot();
		Path tmp = Files.createTempDirectory("mlr-10min-redir");
		Path out = tmp.resolve("newfile.csv");
		new MlrBinder("mlr", root.toString())
				.flag(Flags.icsv())
				.flag(Flags.opprint())
				.verb(Verbs.cat())
				.file("example.csv")
				.redirectOutputFile(out.toFile())
				.run();
		assertEquals(expected("cat_icsv_opprint.txt"), Files.readString(out).trim());
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void inPlaceSort() throws Exception {
		Path root = tenMinRoot();
		Path tmp = Files.createTempDirectory("mlr-10min-inplace");
		Path nf = tmp.resolve("newfile.txt");
		Files.copy(root.resolve("example.csv"), nf);
		new MlrBinder("mlr", tmp.toString())
				.flag(Flags.inPlaceShort())
				.flag(Flags.csv())
				.verb(Verbs.sort(new Flag("-f").objective("shape")))
				.file("newfile.txt")
				.run();
		assertEquals(expected("inplace_sorted_by_shape.txt"), Files.readString(nf).trim());
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void splitByShape() throws Exception {
		Path root = tenMinRoot();
		Path tmp = Files.createTempDirectory("mlr-10min-split");
		Files.copy(root.resolve("example.csv"), tmp.resolve("example.csv"));
		new MlrBinder("mlr", tmp.toString())
				.flag(Flags.csv())
				.flag(Flags.from("example.csv"))
				.verb(Verbs.split(new Flag("-g").objective("shape")))
				.run();
		assertEquals(expected("split/split_circle.csv"),
				Files.readString(tmp.resolve("split_circle.csv")).trim());
		assertEquals(expected("split/split_square.csv"),
				Files.readString(tmp.resolve("split_square.csv")).trim());
		assertEquals(expected("split/split_triangle.csv"),
				Files.readString(tmp.resolve("split_triangle.csv")).trim());
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void teeInsidePut() throws Exception {
		Path root = tenMinRoot();
		Path tmp = Files.createTempDirectory("mlr-10min-tee");
		Files.copy(root.resolve("example.csv"), tmp.resolve("example.csv"));
		new MlrBinder("mlr", tmp.toString())
				.flag(Flags.csv())
				.flag(Flags.from("example.csv"))
				.verb(Verbs.put(new Flag("-q"), objective("tee > $shape.\".csv\", $*")))
				.run();
		assertEquals(expected("tee/circle.csv"), Files.readString(tmp.resolve("circle.csv")).trim());
		assertEquals(expected("tee/square.csv"), Files.readString(tmp.resolve("square.csv")).trim());
		assertEquals(expected("tee/triangle.csv"), Files.readString(tmp.resolve("triangle.csv")).trim());
	}
}
