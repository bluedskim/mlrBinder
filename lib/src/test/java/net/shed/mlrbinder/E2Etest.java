package net.shed.mlrbinder;

import static net.shed.mlrbinder.Flags.csv;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Verbs.cat;
import static net.shed.mlrbinder.verb.Verbs.head;
import static net.shed.mlrbinder.verb.Verbs.sort;
import static net.shed.mlrbinder.verb.Verbs.tac;
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

import net.shed.mlrbinder.verb.Option;
import net.shed.mlrbinder.verb.Verb;
import net.shed.mlrbinder.verb.Verbs;

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

		MlrBinder mlr = new MlrBinder("mlr", workingPath)
		.flag(new Flag("--icsv"))
		.flag(new Flag("--ocsv"))
		.verb(new Verb("cat"))
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

		MlrBinder mlr = new MlrBinder("mlr", workingPath)
		.flag(new Flag("--csv"))
		.verb(new Verb("cat"))
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

		MlrBinder mlr = new MlrBinder("mlr", workingPath)
		.flag(new Flag("--csv"))
		.verb(
			new Verb("sort")
			.addArg(
				new Option(
					new Flag("-f")
					,new Objective("c")
				)
			)
			.addArg(
				new Option(
					new Flag("-f")
					,new Objective("a")
				)
			)
		)
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

		MlrBinder mlr = new MlrBinder("mlr", workingPath)
		.flag(new Flag("--icsv"))
		.flag(new Flag("--ocsv"))
		.verb(
			new Verb("cut")
			.addArg(
				new Option(
					new Flag("-o")
				)
			).addArg(
				new Option(
					new Flag("-f").objective(
						new Objective("b,c")
					)
				)
			)
		)
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

		MlrBinder mlr = new MlrBinder("mlr", workingPath)
		.flag(new Flag("--icsv"))
		.flag(new Flag("--ocsv"))
		.verb(
			new Verb("put")
			.addArg(
				new Objective("'$[[[3]]] = \"7\"'")
			)
		)
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

		MlrBinder mlr = new MlrBinder("mlr", workingPath)
			.workingPath(workingPath)
			.flag(csv())
			.verb(cat())
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
		MlrBinder mlr = new MlrBinder("mlr", workingPath)
			.workingPath(workingPath)
			.flag(csv())
			.verb(
				sort()
					.addArg(new Option(new Flag("-n"), new Objective("a")))
					.addArg(new Option(new Flag("-nr"), new Objective("b")))
			)
			.file("example.csv")
		;
		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv sort -n a -nr b example.csv", mlr.toString());
		 */

		MlrBinder mlr = new MlrBinder()
			.workingPath(workingPath)
			.flag(csv())
			.verb(
				sort(
					new Flag("-n").objective("a")
					,new Flag("-nr").objective("b")
				)
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
		MlrBinder mlr = new MlrBinder("mlr", workingPath)
			.flag(csv())
			.verb(cat())
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

		MlrBinder mlr = MlrBinder.csv()
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

		MlrBinder mlr = new MlrBinder("mlr", workingPath)
			.flag(new Flag("--csv"))
			.verb(
				head(new Option(new Flag("-n"), new Objective("2"))),
				tac())
			.file("example.csv");

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv head -n 2 then tac example.csv", mlr.toString());
		String runResult = mlr.run();
		assertEquals("1,2,3\n4,5,6\na,b,c", runResult);
	}

	@Test
	public void mfromMultiFileCatTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();

		MlrBinder mlr = new MlrBinder("mlr", workingPath)
			.flag(new Flag("--csv"))
			.mfrom("example.csv", "example.csv")
			.verb(cat());

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr --csv --mfrom example.csv example.csv -- cat", mlr.toString());
		String runResult = mlr.run();
		assertEquals(
				"a,b,c\n4,5,6\n1,2,3\n9,8,7\na,b,c\n4,5,6\n1,2,3\n9,8,7",
				runResult);
	}
	}

	@Test
	public void verbsDelegatesToSameAsVerbStatic() {
		assertEquals(Verbs.sort().toString(), Verb.sort().toString());
		assertEquals(Verbs.cat(new Flag("-n")).toString(), Verb.cat(new Flag("-n")).toString());
	}
}
