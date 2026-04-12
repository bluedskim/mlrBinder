package net.shed.mlrbinder;

import static net.shed.mlrbinder.Flag.flag;
import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Option.option;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

public class E2Etest {
	private static Logger logger = Logger.getLogger(E2Etest.class.getName());

	static boolean mlrOnPath() {
		try {
			Process p = new ProcessBuilder("mlr", "--version").redirectErrorStream(true).start();
			boolean finished = p.waitFor(10, TimeUnit.SECONDS);
			return finished && p.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	@Nested
	@EnabledIf("net.shed.mlrbinder.E2Etest#mlrOnPath")
	class MlrIntegration {
	@Test
	public void multiFlagTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		Mlr mlr = Mlr.inDir(workingPath)
		.icsv()
		.ocsv()
		.cat()
		.file("example.csv")
		;

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --icsv --ocsv cat example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("a,b,c\n4,5,6\n1,2,3\n9,8,7", runResult);
	}

	@Test
	public void catTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		Mlr mlr = Mlr.inDir(workingPath)
		.csvFlag()
		.cat()
		.file("example.csv")
		;

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv cat example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("a,b,c\n4,5,6\n1,2,3\n9,8,7", runResult);
	}

	@Test
	public void sortTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		Mlr mlr = Mlr.inDir(workingPath)
		.csvFlag()
		.sort(
			option(flag("-f"), objective("c")),
			option(flag("-f"), objective("a")))
		.file("example.csv")
		;

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv sort -f c -f a example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("a,b,c\n1,2,3\n4,5,6\n9,8,7", runResult);
	}

	@Test
	public void cutTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		Mlr mlr = Mlr.inDir(workingPath)
		.icsv()
		.ocsv()
		.cut(
			option(flag("-o")),
			option(flag("-f").objective(objective("b,c"))))
		.file("example.csv")
		;

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --icsv --ocsv cut -o -f b,c example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("b,c\n5,6\n2,3\n8,7", runResult);
	}

	@Test
	public void putTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		Mlr mlr = Mlr.inDir(workingPath)
		.icsv()
		.ocsv()
		.put(objective("'$[[[3]]] = \"7\"'"))
		.file("example.csv")
		;

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --icsv --ocsv put '$[[[3]]] = \"7\"' example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("a,b,c\n4,5,7\n1,2,7\n9,8,7", runResult);
	}

	@Test
	public void staticCatMethodTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		Mlr mlr = Mlr.inDir(workingPath)
			.workingPath(workingPath)
			.csvFlag()
			.cat()
			.file("example.csv")
		;

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv cat example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("a,b,c\n4,5,6\n1,2,3\n9,8,7", runResult);
	}

	@Test
	public void staticSortMethodTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		/*
		Mlr mlr = Mlr.inDir(workingPath)
			.workingPath(workingPath)
			.flag(csv())
			.verb(
				sort()
					.addArg(option(flag("-n"), objective("a")))
					.addArg(option(flag("-nr"), objective("b")))
			)
			.file("example.csv")
		;
		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv sort -n a -nr b example.csv", mlr.toString());
		 */

		Mlr mlr = Mlr.mlr()
			.workingPath(workingPath)
			.csvFlag()
			.sort(
					n("a")
					,nr("b")
			)
			.file("example.csv")
		;
		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv sort -n a -nr b example.csv", mlr.toString());

		String runResult = mlr.run();
		assertEquals("a,b,c\n1,2,3\n4,5,6\n9,8,7", runResult);
	}

	@Test
	public void stdinReaderCatTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();
		File out = File.createTempFile("mlrbinder-stdin", ".csv");
		out.deleteOnExit();

		String stdinCsv = "a,b,c\n4,5,6\n1,2,3\n9,8,7\n";
		Mlr mlr = Mlr.inDir(workingPath)
			.csvFlag()
			.cat()
			.redirectOutputFile(out);

		mlr.run(new InputStreamReader(new ByteArrayInputStream(stdinCsv.getBytes(StandardCharsets.UTF_8)),
				StandardCharsets.UTF_8));

		String written = Files.readString(out.toPath()).trim();
		assertEquals("a,b,c\n4,5,6\n1,2,3\n9,8,7", written);
	}

	@Test
	public void fluentCsvSortFileTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();
		File example = new File(workingPath, "example.csv");

		Mlr mlr = Mlr.csv()
			.workDir(workingPath)
			.sort(n("a"), nr("b"))
			.file(example);

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv sort -n a -nr b example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("a,b,c\n1,2,3\n4,5,6\n9,8,7", runResult);
	}

	@Test
	public void chainedVerbsHeadThenTacTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		Mlr mlr = Mlr.inDir(workingPath)
			.csvFlag()
			.head(2)
			.tac()
			.file("example.csv");

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv head -n 2 then tac example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("a,b,c\n1,2,3\n4,5,6", runResult);
	}

	@Test
	public void mfromMultiFileCatTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		Mlr mlr = Mlr.inDir(workingPath)
			.csvFlag()
			.mfrom("example.csv", "example.csv")
			.cat();

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv --mfrom example.csv example.csv -- cat", mlr.toString());
		String runResult = mlr.run();
		assertEquals("a,b,c\n4,5,6\n1,2,3\n9,8,7\n4,5,6\n1,2,3\n9,8,7", runResult);
	}
	}

	@Test
	public void verbsDelegatesToSameAsVerbStatic() {
		assertEquals(net.shed.mlrbinder.verb.Verbs.sort().toString(), Mlr.Verbs.sort().toString());
		assertEquals(net.shed.mlrbinder.verb.Verbs.cat(flag("-n")).toString(), Mlr.Verbs.cat(flag("-n")).toString());
	}
}
