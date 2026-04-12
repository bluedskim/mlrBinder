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

/**
 * Miller 10min "File formats" section: same tiny shape dataset in CSV, JSON array, JSONL, DKVP, TSV.
 */
class TenMinTutorialFormatsE2eTest {

	static boolean mlrOnPath() {
		try {
			Process p = new ProcessBuilder("mlr", "--version").redirectErrorStream(true).start();
			boolean finished = p.waitFor(10, TimeUnit.SECONDS);
			return finished && p.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private static Path formatsRoot() throws URISyntaxException {
		var url = TenMinTutorialFormatsE2eTest.class.getClassLoader().getResource("10min/formats/shape.csv");
		if (url == null) {
			throw new IllegalStateException("missing test resource 10min/formats/shape.csv");
		}
		return Paths.get(url.toURI()).getParent();
	}

	private static String expected(String name) throws IOException, URISyntaxException {
		return Files.readString(formatsRoot().resolve("expected").resolve(name)).trim();
	}

	private static String run(Mlr mlr) throws IOException, InterruptedException {
		return mlr.run().trim();
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialFormatsE2eTest#mlrOnPath")
	void csvCat() throws Exception {
		Path root = formatsRoot();
		Mlr mlr = Mlr.inDir(root.toString()).withCsv().cat().file("shape.csv");
		assertEquals(expected("shape_csv_cat.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialFormatsE2eTest#mlrOnPath")
	void jsonArrayCat() throws Exception {
		Path root = formatsRoot();
		Mlr mlr = Mlr.inDir(root.toString()).json().cat().file("shape.json");
		assertEquals(expected("shape_json_cat.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialFormatsE2eTest#mlrOnPath")
	void jsonLinesCat() throws Exception {
		Path root = formatsRoot();
		Mlr mlr = Mlr.inDir(root.toString()).json().cat().file("shape.jsonl");
		assertEquals(expected("shape_jsonl_cat.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialFormatsE2eTest#mlrOnPath")
	void dkvpToCsv() throws Exception {
		Path root = formatsRoot();
		Mlr mlr = Mlr.inDir(root.toString()).idkvp().ocsv().cat().file("shape.dkvp");
		assertEquals(expected("shape_dkvp_to_csv.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialFormatsE2eTest#mlrOnPath")
	void tsvCat() throws Exception {
		Path root = formatsRoot();
		Mlr mlr = Mlr.inDir(root.toString()).tsv().cat().file("shape.tsv");
		assertEquals(expected("shape_tsv_cat.txt"), run(mlr));
	}

	@Test
	@EnabledIf("net.shed.mlrbinder.TenMinTutorialFormatsE2eTest#mlrOnPath")
	void jsonToCsvTwoRecords() throws Exception {
		Path root = formatsRoot();
		Mlr mlr = Mlr.inDir(root.toString()).ijson().ocsv().cat().file("shape.json");
		assertEquals("shape,flag,index\n" + "circle,1,24\n" + "square,0,36", run(mlr));
	}
}
