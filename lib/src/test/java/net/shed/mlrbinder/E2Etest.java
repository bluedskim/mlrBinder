package net.shed.mlrbinder;

import static net.shed.mlrbinder.Flag.csv;
import static net.shed.mlrbinder.verb.Verb.cat;
import static net.shed.mlrbinder.verb.Verb.sort;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import net.shed.mlrbinder.verb.Option;
import net.shed.mlrbinder.verb.Verb;

public class E2Etest {
	private static Logger logger = Logger.getLogger(E2Etest.class.getName());
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
}
