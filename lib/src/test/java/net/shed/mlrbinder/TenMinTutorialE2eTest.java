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

import static net.shed.mlrbinder.Flag.flag;
import static net.shed.mlrbinder.Flags.c2p;
import static net.shed.mlrbinder.Flags.csv;
import static net.shed.mlrbinder.Flags.from;
import static net.shed.mlrbinder.Flags.icsv;
import static net.shed.mlrbinder.Flags.ijson;
import static net.shed.mlrbinder.Flags.inPlaceShort;
import static net.shed.mlrbinder.Flags.ixtab;
import static net.shed.mlrbinder.Flags.json;
import static net.shed.mlrbinder.Flags.ocsv;
import static net.shed.mlrbinder.Flags.ojson;
import static net.shed.mlrbinder.Flags.opprint;
import static net.shed.mlrbinder.Flags.oxtab;
import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.f;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Option.option;
import static net.shed.mlrbinder.Mlr.Verbs.cat;
import static net.shed.mlrbinder.Mlr.Verbs.cut;
import static net.shed.mlrbinder.Mlr.Verbs.filter;
import static net.shed.mlrbinder.Mlr.Verbs.head;
import static net.shed.mlrbinder.Mlr.Verbs.put;
import static net.shed.mlrbinder.Mlr.Verbs.sort;
import static net.shed.mlrbinder.Mlr.Verbs.stats1;
import static net.shed.mlrbinder.Mlr.Verbs.tail;

/**
 * End-to-end checks that {@link Mlr} builds argv matching the Miller
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

	private static String run(Mlr binder) throws IOException, InterruptedException {
		return binder.run().trim();
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void catCsv() throws Exception {
		Path root = tenMinRoot();
		Mlr mlr = Mlr.inDir(root.toString())
				.flag(csv())
				.verb(cat())
				.file("example.csv");
		assertEquals(expected("cat_csv.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void catIcsvOpprint() throws Exception {
		Path root = tenMinRoot();
		Mlr mlr = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(cat())
				.file("example.csv");
		assertEquals(expected("cat_icsv_opprint.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void headAndTail() throws Exception {
		Path root = tenMinRoot();
		Mlr head = Mlr.inDir(root.toString())
				.flag(csv())
				.verb(head(option(n(), objective("4"))))
				.file("example.csv");
		assertEquals(expected("head_n4.txt"), run(head));

		Mlr tail = Mlr.inDir(root.toString())
				.flag(csv())
				.verb(tail(option(n(), objective("4"))))
				.file("example.csv");
		assertEquals(expected("tail_n4.txt"), run(tail));

		Mlr tailJson = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(ojson())
				.verb(tail(option(n(), objective("2"))))
				.file("example.csv");
		assertEquals(expected("tail_n2_json.txt"), run(tailJson));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void sortAndCut() throws Exception {
		Path root = tenMinRoot();
		Mlr sortF = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(sort(f("shape")))
				.file("example.csv");
		assertEquals(expected("sort_f_shape.txt"), run(sortF));

		Mlr sortFnr = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(sort(f("shape"), nr("index")))
				.file("example.csv");
		assertEquals(expected("sort_f_shape_nr_index.txt"), run(sortFnr));

		Mlr cutKeep = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(cut(option(flag("-f").objective("flag,shape"))))
				.file("example.csv");
		assertEquals(expected("cut_f.txt"), run(cutKeep));

		Mlr cutOrdered = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(cut(
						option(flag("-o")),
						option(flag("-f").objective("flag,shape"))))
				.file("example.csv");
		assertEquals(expected("cut_o_f.txt"), run(cutOrdered));

		Mlr cutOmit = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(cut(
						option(flag("-x")),
						option(flag("-f").objective("flag,shape"))))
				.file("example.csv");
		assertEquals(expected("cut_x_f.txt"), run(cutOmit));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void putPositionalFieldNamesAndValues() throws Exception {
		Path root = tenMinRoot();
		Mlr rename = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(put(objective("$[[3]] = \"NEW\"")))
				.file("example.csv");
		assertEquals(expected("put_rename_field3.txt"), run(rename));

		Mlr value = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(put(objective("$[[[3]]] = \"NEW\"")))
				.file("example.csv");
		assertEquals(expected("put_value_field3.txt"), run(value));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void filterAndPutComputed() throws Exception {
		Path root = tenMinRoot();
		Mlr f1 = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(filter(objective("$color == \"red\"")))
				.file("example.csv");
		assertEquals(expected("filter_color_red.txt"), run(f1));

		Mlr f2 = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(filter(objective("$color == \"red\" && $flag == \"true\"")))
				.file("example.csv");
		assertEquals(expected("filter_red_true.txt"), run(f2));

		String putExpr = "$ratio = $quantity / $rate; $color_shape = $color . \"_\" . $shape";
		Mlr put = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(put(objective(putExpr)))
				.file("example.csv");
		assertEquals(expected("put_ratio_color_shape.txt"), run(put));

		// Tutorial shows $y2 (typo); Miller uses ** for exponent — see tutorial prose / corrected samples.
		String yzExpr = "$y = $index + 1; $z = $y**2 + $k";
		Mlr fromPut = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.flag(from("example.csv"))
				.verb(put(objective(yzExpr)));
		assertEquals(expected("put_y_z.txt"), run(fromPut));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void spacesFieldNames() throws Exception {
		Path root = tenMinRoot();
		Mlr spacesCat = Mlr.inDir(root.toString())
				.flag(csv())
				.verb(cat())
				.file("spaces.csv");
		assertEquals(expected("spaces_cat_csv.txt"), run(spacesCat));

		Mlr sort = Mlr.inDir(root.toString())
				.flag(c2p())
				.verb(sort(nr("Total MWh")))
				.file("spaces.csv");
		assertEquals(expected("spaces_sort.txt"), run(sort));

		Mlr put = Mlr.inDir(root.toString())
				.flag(c2p())
				.verb(put(objective("${Total KWh} = ${Total MWh} * 1000")))
				.file("spaces.csv");
		assertEquals(expected("spaces_put_kwh.txt"), run(put));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void multipleInputFiles() throws Exception {
		Path root = tenMinRoot();
		Mlr mlr = Mlr.inDir(root.toString())
				.flag(csv())
				.verb(cat())
				.file("data/a.csv")
				.file("data/b.csv");
		assertEquals(expected("multi_cat.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void thenChainingAndFrom() throws Exception {
		Path root = tenMinRoot();
		Mlr chained = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(
						sort(nr("index")),
						head(option(n(), objective("3"))))
				.file("example.csv");
		assertEquals(expected("sort_then_head.txt"), run(chained));

		// Tutorial: shell pipe "mlr --csv sort -nr index ... | mlr --icsv --opprint head -n 3"
		Path pipeTmp = Files.createTempDirectory("mlr-10min-pipe");
		Path sortedCsv = pipeTmp.resolve("sorted.csv");
		Mlr.inDir(root.toString())
				.flag(csv())
				.verb(sort(nr("index")))
				.file("example.csv")
				.redirectOutputFile(sortedCsv.toFile())
				.run();
		Mlr second = Mlr.inDir(pipeTmp.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(head(option(n(), objective("3"))))
				.file(sortedCsv.getFileName().toString());
		assertEquals(expected("sort_then_head.txt"), run(second));

		Mlr fromHead = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.flag(from("example.csv"))
				.verb(
						sort(nr("index")),
						head(option(n(), objective("3"))));
		assertEquals(expected("from_sort_then_head.txt"), run(fromHead));

		Mlr fromChainCut = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.flag(from("example.csv"))
				.verb(
						sort(nr("index")),
						head(option(n(), objective("3"))),
						cut(option(flag("-f").objective("shape,quantity"))));
		assertEquals(expected("from_chain_cut.txt"), run(fromChainCut));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void headGroupByAndStats1() throws Exception {
		Path root = tenMinRoot();
		Mlr headG = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(
						sort(f("shape"), nr("index")),
						head(
								option(n(), objective("1")),
								option(flag("-g"), objective("shape"))))
				.file("example.csv");
		assertEquals(expected("head_g_shape.txt"), run(headG));

		Mlr statsShape = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.flag(from("example.csv"))
				.verb(stats1(
						flag("-a").objective("count,min,mean,max"),
						flag("-f").objective("quantity"),
						flag("-g").objective("shape")));
		assertEquals(expected("stats1_g_shape.txt"), run(statsShape));

		Mlr statsShapeColor = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.flag(from("example.csv"))
				.verb(stats1(
						flag("-a").objective("count,min,mean,max"),
						flag("-f").objective("quantity"),
						flag("-g").objective("shape,color")));
		assertEquals(expected("stats1_g_shape_color.txt"), run(statsShapeColor));

		Mlr statsXtab = Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(oxtab())
				.flag(from("example.csv"))
				.verb(stats1(
						flag("-a").objective("p0,p10,p25,p50,p75,p90,p99,p100"),
						flag("-f").objective("rate")));
		assertEquals(expected("stats1_rate_percentiles_xtab.txt"), run(statsXtab));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void unicodeSamples() throws Exception {
		Path root = tenMinRoot();
		Mlr greekFilter = Mlr.inDir(root.toString())
				.flag(c2p())
				.verb(filter(objective("$σχήμα == \"κύκλος\"")))
				.file("παράδειγμα.csv");
		assertEquals(expected("greek_filter_circles.txt"), run(greekFilter));

		Mlr greekSort = Mlr.inDir(root.toString())
				.flag(c2p())
				.verb(sort(f("σημαία")))
				.file("παράδειγμα.csv");
		assertEquals(expected("greek_sort_flag.txt"), run(greekSort));

		Mlr ruPut = Mlr.inDir(root.toString())
				.flag(c2p())
				.verb(put(objective("$форма = toupper($форма); $длина = strlen($цвет)")))
				.file("пример.csv");
		assertEquals(expected("russian_put_toupper_strlen.txt"), run(ruPut));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void jsonAndNestedConversion() throws Exception {
		Path root = tenMinRoot();
		Mlr jsonCat = Mlr.inDir(root.toString())
				.flag(json())
				.verb(cat())
				.file("example.json");
		assertEquals(expected("json_cat.txt"), run(jsonCat));

		Mlr jsonCsv = Mlr.inDir(root.toString())
				.flag(ijson())
				.flag(ocsv())
				.verb(cat())
				.file("example.json");
		assertEquals(expected("json_to_csv.txt"), run(jsonCsv));

		Mlr srvCsv = Mlr.inDir(root.toString())
				.flag(ijson())
				.flag(ocsv())
				.verb(cat())
				.file("data/server-log.json");
		assertEquals(expected("server_log_to_csv.txt"), run(srvCsv));

		Mlr srvXtab = Mlr.inDir(root.toString())
				.flag(ijson())
				.flag(oxtab())
				.verb(cat())
				.file("data/server-log.json");
		assertEquals(expected("server_log_to_xtab.txt"), run(srvXtab));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void jsonXtabRoundTrip() throws Exception {
		Path root = tenMinRoot();
		Path tmp = Files.createTempDirectory("mlr-10min-xtab");
		Path xtabFile = tmp.resolve("sl.xtab");
		Mlr.inDir(root.toString())
				.flag(ijson())
				.flag(oxtab())
				.verb(cat())
				.file("data/server-log.json")
				.redirectOutputFile(xtabFile.toFile())
				.run();

		Mlr back = Mlr.inDir(tmp.toString())
				.flag(ixtab())
				.flag(ojson())
				.verb(cat())
				.file(xtabFile.getFileName().toString());
		assertEquals(expected("json_xtab_roundtrip.txt"), run(back));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialE2eTest#mlrOnPath")
	void redirectOutputToFile() throws Exception {
		Path root = tenMinRoot();
		Path tmp = Files.createTempDirectory("mlr-10min-redir");
		Path out = tmp.resolve("newfile.csv");
		Mlr.inDir(root.toString())
				.flag(icsv())
				.flag(opprint())
				.verb(cat())
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
		Mlr.inDir(tmp.toString())
				.flag(inPlaceShort())
				.flag(csv())
				.verb(sort(f("shape")))
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
		Mlr.inDir(tmp.toString())
				.flag(csv())
				.flag(from("example.csv"))
				.splitBy("shape")
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
		Mlr.inDir(tmp.toString())
				.flag(csv())
				.flag(from("example.csv"))
				.verb(put(flag("-q"), objective("tee > $shape.\".csv\", $*")))
				.run();
		assertEquals(expected("tee/circle.csv"), Files.readString(tmp.resolve("circle.csv")).trim());
		assertEquals(expected("tee/square.csv"), Files.readString(tmp.resolve("square.csv")).trim());
		assertEquals(expected("tee/triangle.csv"), Files.readString(tmp.resolve("triangle.csv")).trim());
	}
}
