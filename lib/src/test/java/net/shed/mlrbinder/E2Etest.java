package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import net.shed.mlrbinder.verb.Verb;

public class E2Etest {
	private static Logger logger = Logger.getLogger(E2Etest.class.getName());

	@Test
	public void catTest() throws IOException, InterruptedException {
		String workingPath = getClass().getClassLoader().getResource("csv").getFile().toString();
		MlrBinder mlr = new MlrBinder("mlr", workingPath);

		logger.info("mlr.workingPath=" + mlr.workingPath);

		Flag csv = new Flag("--csv");
		mlr.flag(csv);
		Verb cat = new Verb("cat");
		mlr.verb(cat);
		mlr.file("example.csv");

		logger.info("mlr=" + mlr.toString());

		String runResult = mlr.run();
		assertEquals("color,shape,flag,k,index,quantity,rate\nyellow,triangle,true,1,11,43.6498,9.8870", runResult);
	}
}
