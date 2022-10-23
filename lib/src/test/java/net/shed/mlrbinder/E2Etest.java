package net.shed.mlrbinder;

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
		.flag(new Flag("-icsv"))
		.flag(new Flag("-ocsv"))
		.verb(new Verb("cat"))
		.file("example.csv")
		;

		logger.info("mlr=" + mlr.toString());
		assertEquals("mlr -icsv -ocsv cat example.csv", mlr.toString());
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
			.option(
				new Option(
					new Flag("-f")
					,new Object("c")
				)
			)
			.option(
				new Option(
					new Flag("-f")
					,new Object("a")
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
}
