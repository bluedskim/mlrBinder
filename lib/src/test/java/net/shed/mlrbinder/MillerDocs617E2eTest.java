package net.shed.mlrbinder;

import static net.shed.mlrbinder.CutFlags.f;
import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.StatsFlags.aggregations;
import static net.shed.mlrbinder.StatsFlags.field;
import static net.shed.mlrbinder.StatsFlags.groupBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

/**
 * End-to-end checks for representative {@code mlr} samples from the Miller 6.17.0
 * <a href="https://miller.readthedocs.io/en/6.17.0/">FAQs and examples</a> documentation.
 * <p>
 * Golden files live under {@code src/test/resources/miller-docs-6.17/expected/}. Regenerate with
 * {@code MLR=/path/to/mlr-6.17.0 ./utils/gen_miller_docs_617_goldens.sh} after changing fixtures or samples.
 * </p>
 * <p>
 * Chains follow the recommended style: global I/O on {@link Mlr} (e.g. {@link Mlr#csv()} after
 * {@link Mlr#inDir(String)} for {@code --csv}; use {@link Mlr#withCsvPreset()} when starting from the CSV preset),
 * then verb-named methods.
 * </p>
 */
class MillerDocs617E2eTest {

	static boolean mlr617OnPath() {
		try {
			Process p = new ProcessBuilder("mlr", "--version").redirectErrorStream(true).start();
			boolean finished = p.waitFor(10, TimeUnit.SECONDS);
			if (!finished || p.exitValue() != 0) {
				return false;
			}
			String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
			return out.contains("6.17.0");
		} catch (Exception e) {
			return false;
		}
	}

	private static Path docRoot() throws URISyntaxException {
		URL url = MillerDocs617E2eTest.class.getClassLoader().getResource("miller-docs-6.17/data/headerless.csv");
		if (url == null) {
			throw new IllegalStateException("missing test resource miller-docs-6.17/data/headerless.csv");
		}
		return Paths.get(url.toURI()).getParent();
	}

	private static String expected(String name) throws IOException, URISyntaxException {
		Path p = docRoot().resolve("..").resolve("expected").resolve(name + ".txt").normalize();
		return Files.readString(p).trim();
	}

	private static String run(Mlr binder) throws IOException, InterruptedException {
		return binder.run().trim();
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class CsvWithAndWithoutHeaders {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/csv-with-and-without-headers/")
		void implicitHeaderCat() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.implicitCsvHeader()
					.cat()
					.file("headerless.csv");
			assertEquals(expected("csv_implicit_header_cat"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/csv-with-and-without-headers/")
		void implicitHeaderLabel() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.implicitCsvHeader()
					.cat()
					.label(objective("name,age,status"))
					.file("headerless.csv");
			assertEquals(expected("csv_implicit_header_label"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/csv-with-and-without-headers/")
		void dkvpSubsetHeaderlessCsvOutput() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.idkvp()
					.ocsv()
					.headerlessCsvOutput()
					.cat()
					.file("colored-shapes-head5.dkvp");
			assertEquals(expected("dkvp_head5_headerless_csv"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/csv-with-and-without-headers/")
		void nidxCutOxTab() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.inidx()
					.ifs("comma")
					.oxtab()
					.cutFields("1,3")
					.file("headerless.csv");
			assertEquals(expected("nidx_cut_13_headerless"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/csv-with-and-without-headers/")
		void nasHiCat() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.hi()
					.cat()
					.file("nas.csv");
			assertEquals(expected("nas_hi_cat"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/csv-with-and-without-headers/")
		void nasNidxOcsv() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.inidx()
					.ifs("comma")
					.ocsv()
					.cat()
					.file("nas.csv");
			assertEquals(expected("nas_inidx_ocsv_cat"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/csv-with-and-without-headers/")
		void nasHiLabelChain() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.hi()
					.cat()
					.label(objective("xsn,ysn,x,y,t,a,e29,e31,e32"))
					.file("nas.csv");
			assertEquals(expected("nas_hi_label_chain"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/csv-with-and-without-headers/")
		void raggedPadPut() throws Exception {
			Path root = docRoot();
			String expr = "@maxnf = max(@maxnf, NF);\n"
					+ "while(NF < @maxnf) {\n"
					+ "  $[NF+1] = \"\";\n"
					+ "}\n";
			Mlr mlr = Mlr.inDir(root.toString())
					.from("ragged.csv")
					.fs("comma")
					.nidx()
					.put(objective(expr));
			assertEquals(expected("ragged_pad_put"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class ShapesOfData {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/shapes-of-data/")
		void coloursSemicolonFsCut() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.ifs("semicolon")
					.cutFields("KEY,PL,TO")
					.file("colours.csv");
			assertEquals(expected("colours_cut_semicolon"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/shapes-of-data/")
		void cutOrderedFields() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.cutOrdered("rate,shape,flag")
					.file("example.csv");
			assertEquals(expected("example_cut_ordered_rate_shape_flag"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/shapes-of-data/")
		void filterYellowThenCatN() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.filterVerb(objective("$color == \"yellow\""))
					.catNumbered()
					.file("example.csv");
			assertEquals(expected("example_filter_yellow_cat_n"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/shapes-of-data/")
		void rectangularizeRectPut() throws Exception {
			Path root = docRoot();
			String expr = "is_present($outer) {\n"
					+ "  unset @r\n"
					+ "}\n"
					+ "for (k, v in $*) {\n"
					+ "  @r[k] = v\n"
					+ "}\n"
					+ "is_present($inner1) {\n"
					+ "  emit @r\n"
					+ "}\n";
			Mlr mlr = Mlr.inDir(root.toString())
					.from("rect.txt")
					.putQuiet(objective(expr));
			assertEquals(expected("rect_emit_put"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class OperatingOnAllFields {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/operating-on-all-fields/")
		void bulkRenameSpaces() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.renameGlobalRegex(" ,_")
					.file("spaces.csv");
			assertEquals(expected("spaces_rename_g_r"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/operating-on-all-fields/")
		void headerLfNormalize() throws Exception {
			Path root = docRoot();
			String expr = "map inrec = $*;\n"
					+ "$* = {};\n"
					+ "for (oldkey, value in inrec) {\n"
					+ "  newkey = clean_whitespace(gsub(oldkey, \"\\n\", \" \"));\n"
					+ "  $[newkey] = value;\n"
					+ "}\n";
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.from("header-lf.csv")
					.put(objective(expr));
			assertEquals(expected("header_lf_normalize"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/operating-on-all-fields/")
		void sarPutFromFile() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.putFromFile("sar.mlr")
					.file("sar.csv");
			assertEquals(expected("sar_gsub_all_fields"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/operating-on-all-fields/")
		void smallFullRecordReassign() throws Exception {
			Path root = docRoot();
			String expr = "begin {\n"
					+ "  @i_cumu = 0;\n"
					+ "}\n"
					+ "@i_cumu += $i;\n"
					+ "$* = {\n"
					+ "  \"z\": $x + $y,\n"
					+ "  \"KEYFIELD\": $a,\n"
					+ "  \"i\": @i_cumu,\n"
					+ "  \"b\": $b,\n"
					+ "  \"y\": $x,\n"
					+ "  \"x\": $y,\n"
					+ "};\n";
			Mlr mlr = Mlr.inDir(root.toString()).put(objective(expr)).file("small");
			assertEquals(expected("small_put_reassign"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class OperatingOnAllRecords {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/operating-on-all-records/")
		void shortCsvSumEmitQuiet() throws Exception {
			Path root = docRoot();
			String expr = "begin {\n"
					+ "  @count = 0;\n"
					+ "  @sum = 0;\n"
					+ "}\n"
					+ "@count += 1;\n"
					+ "@sum += $value;\n"
					+ "end {\n"
					+ "  emit (@count, @sum);\n"
					+ "}\n";
			Mlr mlr = Mlr.inDir(root.toString())
					.icsv()
					.ojson()
					.from("short.csv")
					.putQuiet(objective(expr));
			assertEquals(expected("short_sum_put_q"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class ThenChaining {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/questions-about-then-chaining/")
		void countDistinct() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.from("then-example.csv")
					.c2p()
					.countDistinctFields("Status,Payment_Type");
			assertEquals(expected("then_count_distinct"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/questions-about-then-chaining/")
		void countDistinctThenSort() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.from("then-example.csv")
					.c2p()
					.countDistinctFields("Status,Payment_Type")
					.sort(nr("count"));
			assertEquals(expected("then_count_distinct_sort"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/questions-about-then-chaining/")
		void filterThenCatN() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.filterVerb(objective("$x > 0.5"))
					.catNumbered()
					.file("small");
			assertEquals(expected("small_filter_then_cat_n"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class Joins {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/questions-about-joins/")
		void joinUnsortedDefault() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.icsvlite()
					.opprint()
					.joinUnsorted("ipaddr", "join-u-left.csv")
					.file("join-u-right.csv");
			assertEquals(expected("join_u_ipaddr"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/questions-about-joins/")
		void joinLeftUnpairedUnsparsify() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.csv()
					.joinLeftRightUnpaired("color", "prevtemp.csv")
					.unsparsifyFillWith("0")
					.put(objective("$count_delta = int($current_count) - int($previous_count)"))
					.file("currtemp.csv");
			assertEquals(expected("join_color_ul_unsparsify"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/questions-about-joins/")
		void multiJoinChain() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.icsv()
					.opprint()
					.joinFile("multi-join/name-lookup.csv", "id")
					.joinFile("multi-join/status-lookup.csv", "id")
					.file("multi-join/input.csv");
			assertEquals(expected("multi_join_chain"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class DateTimeExamples {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/date-time-examples/")
		void filterByDateStrptime() throws Exception {
			Path root = docRoot();
			String expr = "strptime($date, \"%Y-%m-%d\") > strptime(\"2018-03-03\", \"%Y-%m-%d\")";
			Mlr mlr = Mlr.inDir(root.toString()).csv().filterVerb(objective(expr)).file("dates.csv");
			assertEquals(expected("dates_filter_strptime"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/date-time-examples/#finding-missing-dates")
		void missDateStepHead10() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.from("miss-date.csv")
					.icsv()
					.catNumbered()
					.put(objective("$datestamp = strptime($date, \"%Y-%m-%d\")"))
					.stepDelta("datestamp")
					.head(10);
			assertEquals(expected("miss_date_step_head10"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/date-time-examples/#finding-missing-dates")
		void missDateGapsFilter() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.from("miss-date.csv")
					.icsv()
					.catNumbered()
					.put(objective("$datestamp = strptime($date, \"%Y-%m-%d\")"))
					.stepDelta("datestamp")
					.filterVerb(objective("$datestamp_delta != 86400 && $n != 1"));
			assertEquals(expected("miss_date_gaps_filter"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class SpecialSymbols {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/special-symbols-and-formatting/")
		void commasToJson() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString()).icsv().ojson().cat().file("commas.csv");
			assertEquals(expected("commas_icsv_ojson"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/special-symbols-and-formatting/")
		void commasToDkvpPipeOfs() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.icsv()
					.odkvp()
					.ofs("pipe")
					.cat()
					.file("commas.csv");
			assertEquals(expected("commas_icsv_odkvp_ofs_pipe"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/special-symbols-and-formatting/")
		void dkvpCurlyFieldNames() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.dkvp()
					.put(objective("${product.all} = ${x.a} * ${y:b} * ${z/c}"))
					.file("dkvp-curly-in.dkvp");
			assertEquals(expected("dkvp_curly_product"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/special-symbols-and-formatting/")
		void questionMarkGsub() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.oxtab()
					.put(objective("$c = gsub($a, \"[?]\",\" ...\")"))
					.file("question.dat");
			assertEquals(expected("question_gsub_bracket"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/special-symbols-and-formatting/")
		void latin1Utf8Print() throws Exception {
			Path root = docRoot();
			String expr = "end {\n"
					+ "  name = \"Ka\\xf0l\\xedn og \\xdeormundr\";\n"
					+ "  name = gssub(name, \"\\xde\", \"\\u00de\");\n"
					+ "  name = gssub(name, \"\\xf0\", \"\\u00f0\");\n"
					+ "  name = gssub(name, \"\\xed\", \"\\u00ed\");\n"
					+ "  print name;\n"
					+ "}\n";
			Mlr mlr = Mlr.inDir(root.toString()).noInput().put(objective(expr));
			assertEquals(expected("latin1_utf8_print"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class ShellCommands {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/shell-commands/")
		void systemEchoPerRecord() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.opprint()
					.put(objective("$o = system(\"echo hello world\")"))
					.file("small");
			assertEquals(expected("small_system_echo"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class DataCleaning {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/data-cleaning-examples/")
		void booleanCoercion() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.icsv()
					.opprint()
					.put(objective("$reachable = boolean($reachable)"))
					.file("het-bool.csv");
			assertEquals(expected("het_bool_boolean"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class DataDiving {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/data-diving-examples/")
		void flinsHeadC2x() throws Exception {
			Path root = docRoot();
			Assumptions.assumeTrue(Files.exists(root.resolve("flins-subset.csv")));
			Mlr mlr = Mlr.inDir(root.toString()).c2x().from("flins-subset.csv").head(2);
			assertEquals(expected("flins_head2_c2x"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/data-diving-examples/")
		void flinsCountDistinctCounty() throws Exception {
			Path root = docRoot();
			Assumptions.assumeTrue(Files.exists(root.resolve("flins-subset.csv")));
			Mlr mlr = Mlr.inDir(root.toString())
					.c2p()
					.from("flins-subset.csv")
					.countDistinctFields("county");
			assertEquals(expected("flins_count_distinct_county"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class LogProcessing {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/log-processing-examples/")
		void cacheLinesStatsByType() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.idkvp()
					.opprint()
					.stats1(aggregations("mean"), field("hit"), groupBy("type"))
					.sort(f("type"))
					.file("log-sample.dkvp");
			assertEquals(expected("log_stats1_mean_hit_type"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class SqlExamples {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/sql-examples/")
		void mediumUniqCountPairs() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.opprint()
					.uniqCountBy("a,b")
					.sort(nr("count"))
					.file("medium-subset.csv");
			assertEquals(expected("medium_uniq_c_ab"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class KubectlAndHelm {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/kubectl-and-helm/")
		void pprintToJsonFirstRow() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.ipprint()
					.ojson()
					.head(1)
					.file("kubectl-pods-sample.txt");
			assertEquals(expected("kubectl_pods_json_head1"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class DkvpExamples {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/dkvp-examples/")
		void dkvpCatOneRecord() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString()).dkvp().cat().file("stdin-dkvp.dkvp");
			assertEquals(expected("dkvp_stdin_one_line"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class StatisticsExamples {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/statistics-examples/")
		void interquartileRange() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.oxtab()
					.stats1(aggregations("p25,p75"), field("x"))
					.put(objective("$x_iqr = $x_p75 - $x_p25"))
					.file("medium-subset.csv");
			assertEquals(expected("medium_subset_stats1_iqr"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class RandomizingExamples {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/randomizing-examples/")
		void sampleWordsWithSeed() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.seed("42")
					.nidx()
					.from("english-words-sample.txt")
					.filterVerbTyped(objective("n=strlen($1);4<=n&&n<=8"))
					.sampleK(10);
			assertEquals(expected("random_sample_seed42"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class TwoPassAlgorithms {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/two-pass-algorithms/")
		void maxrowsTsvPutScript() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.itsv()
					.opprint()
					.putQuietFromFile("maxrows.mlr")
					.file("maxrows.tsv");
			assertEquals(expected("maxrows_put_q"), run(mlr));
		}

		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/two-pass-algorithms/")
		void featuresJsonPutScript() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.ijson()
					.opprint()
					.putQuietFromFile("feature-count.mlr")
					.file("features.json");
			assertEquals(expected("features_json_put_q"), run(mlr));
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.MillerDocs617E2eTest#mlr617OnPath")
	class ProgrammingExamples {
		@Test
		@DisplayName("https://miller.readthedocs.io/en/6.17.0/programming-examples/")
		void sieveOfEratosthenes() throws Exception {
			Path root = docRoot();
			Mlr mlr = Mlr.inDir(root.toString())
					.noInput()
					.putQuietFromFile("sieve.mlr");
			assertEquals(expected("sieve_n_put"), run(mlr));
		}
	}
}
