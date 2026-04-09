package net.shed.mlrbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.shed.mlrbinder.verb.Option;
import net.shed.mlrbinder.verb.Verb;

class MlrBinderTest {
	private static Logger logger = Logger.getLogger(MlrBinderTest.class.getName());

	@Test
	@DisplayName("mlrPath not nullable test")
	void mlrPathNotNullableTest() {
		MlrBinder mlr = new MlrBinder();
		assertThrows(IllegalArgumentException.class, () -> {
			mlr.toString();
		});
	}

	@Test
	@DisplayName("mlrPath not nullable test")
	void workingPathNullableTest() {
		MlrBinder mlr = new MlrBinder("mlrPath");
		assertThrows(IllegalArgumentException.class, () -> {
			mlr.toString();
		});
	}

	@Test
	void pathTest() {
		String mlrPath = "mlr executable";
		logger.info("mlrPath=" + mlrPath);
		MlrBinder mlr = new MlrBinder().mlrPath(mlrPath);
		logger.info(mlr.getMlrPath());
		assertEquals(mlrPath, mlr.getMlrPath());
	}

	@Test
	@DisplayName("added flags order")
	void flagsTest() {
		int cntOfFlags = new Random().nextInt(10);
		logger.info("cntOfFlags=" + cntOfFlags);
		MlrBinder mlr = new MlrBinder();
		for (int i = 0; i < cntOfFlags; i++) {
			Flag flag = new Flag("flag" + i);
			mlr.flag(flag);
			assertEquals(mlr.getFlags().get(i), flag);
		}
	}

	@Test
	@DisplayName("added flags then toString test")
	void flagsToStringTest() {
		int cntOfFlags = new Random().nextInt(10);
		logger.info("cntOfFlags=" + cntOfFlags);
		MlrBinder mlr = new MlrBinder("mlrPath", "workingPath");
		String toStringResult = "mlrPath";
		for (int i = 0; i < cntOfFlags; i++) {
			Flag flag = new Flag("flag" + i);
			mlr.flag(flag);
			toStringResult += MlrBinder.SPACER + flag;
		}
		logger.info("mlr.toString()=" + mlr.toString());
		assertEquals(mlr.toString(), toStringResult);
	}

	@Test
	@DisplayName("added verb order")
	void verbsTest() {
		int cntOfVerbs = new Random().nextInt(10);
		logger.info("cntOfVerbs=" + cntOfVerbs);
		MlrBinder mlr = new MlrBinder();
		for (int i = 0; i < cntOfVerbs; i++) {
			Verb verb = new Verb("verb" + i);
			mlr.verb(verb);
			assertEquals(mlr.getVerbs().get(i), verb);
		}
	}

	@Test
	@DisplayName("added verbs")
	void verbsMultiTest() {
		int cntOfVerbs = new Random().nextInt(10);
		logger.info("cntOfVerbs=" + cntOfVerbs);
		MlrBinder mlr = new MlrBinder();
		List<Verb> verbs = new ArrayList<>();
		for (int i = 0; i < cntOfVerbs; i++) {
			verbs.add(new Verb("verb" + i));
		}
		mlr.verb(verbs.toArray(new Verb[0]));

		for (int i = 0; i < cntOfVerbs; i++) {
			verbs.add(new Verb("verb" + i));
			assertEquals(mlr.getVerbs().get(i), verbs.get(i));
		}
	}

	@Test
	@DisplayName("added verbs then toString test")
	void verbsToStringTest() {
		int cntOfVerbs = new Random().nextInt(10);
		logger.info("cntOfVerbs=" + cntOfVerbs);
		MlrBinder mlr = new MlrBinder("mlrPath", "workingPath");
		String toStringResult = "mlrPath";
		for (int i = 0; i < cntOfVerbs; i++) {
			Verb verb = new Verb("verb" + i);
			mlr.verb(verb);
			if(i > 0) {
				toStringResult += MlrBinder.SPACER + MlrBinder.CHAINING_ADVERB;
			}
			toStringResult += MlrBinder.SPACER + verb;
		}
		logger.info("mlr.toString()=" + mlr.toString());
		assertEquals(mlr.toString(), toStringResult);
	}

	@Test
	@DisplayName("added files order")
	void filesTest() {
		int cntOfFiles = new Random().nextInt(10);
		logger.info("cntOfFiles=" + cntOfFiles);
		MlrBinder mlr = new MlrBinder();
		for (int i = 0; i < cntOfFiles; i++) {
			String fileName = "file" + i;
			mlr.file(fileName);
			assertEquals(mlr.getFileNames().get(i), fileName);
		}
	}

	@Test
	@DisplayName("added files then toString test")
	void filesToStringTest() {
		int cnt = new Random().nextInt(10);
		logger.info("cnt=" + cnt);
		MlrBinder mlr = new MlrBinder("mlrPath", "workingPath");
		String toStringResult = "mlrPath";
		for (int i = 0; i < cnt; i++) {
			String fileName = "fileName" + i;
			mlr.file(fileName);
			toStringResult += MlrBinder.SPACER + fileName;
		}
		logger.info("mlr.toString()=" + mlr.toString());
		assertEquals(toStringResult, mlr.toString());
	}

	@Test
	@DisplayName("added files, verbs and files then toString test")
	void flagsVerbsFilesToStringTest() {
		int cntOfFlags = new Random().nextInt(10);
		logger.info("cntOfFlags=" + cntOfFlags);
		MlrBinder mlr = new MlrBinder("mlrPath", "workingPath");
		String toStringResult = "mlrPath";
		for (int i = 0; i < cntOfFlags; i++) {
			Flag flag = new Flag("--flag" + i);
			flag.objective(new Objective("obj" + i));
			mlr.flag(flag);
			toStringResult += MlrBinder.SPACER + flag;
		}

		int cntOfVerbs = new Random().nextInt(10);
		logger.info("cntOfVerbs=" + cntOfVerbs);
		for (int i = 0; i < cntOfVerbs; i++) {
			Verb verb = new Verb("verb" + i);
			verb.addArg(
				new Option(
						new Flag("--flag" + i).objective(
								new Objective("object" + i)
					)
				)
			);
			mlr.verb(verb);
			if(i > 0) {
				toStringResult += MlrBinder.SPACER + MlrBinder.CHAINING_ADVERB;
			}
			toStringResult += MlrBinder.SPACER + verb;
		}

		int cnt = new Random().nextInt(10);
		logger.info("cnt=" + cnt);
		for (int i = 0; i < cnt; i++) {
			String fileName = "fileName" + i;
			mlr.file(fileName);
			toStringResult += MlrBinder.SPACER + fileName;
		}

		logger.info("mlr.toString()=" + mlr.toString());
		assertEquals(toStringResult, mlr.toString());
	}

	@Test
	void runSuccess() throws IOException, InterruptedException {
		int exitCode = 1;
		String runResult = "결과";
		ProcessBuilder processBuilder = getProcessBuilder(exitCode, runResult);

		MlrBinder mlr = new MlrBinder(processBuilder);
		mlr.workingPath("workingPath");

		assertEquals(runResult, mlr.run());
		verify(processBuilder).directory(any(File.class));
		verify(processBuilder).command(anyList());
	}

	@Test
	void runStdinPipeFailureThrowsIOException() throws Exception {
		ProcessBuilder processBuilder = mock(ProcessBuilder.class);
		Process process = mock(Process.class);
		when(processBuilder.start()).thenReturn(process);
		when(process.pid()).thenReturn(1L);
		when(process.waitFor()).thenReturn(0);
		when(process.getInputStream()).thenReturn(InputStream.nullInputStream());
		OutputStream failingStdin = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				throw new IOException("pipe failed");
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				throw new IOException("pipe failed");
			}
		};
		when(process.getOutputStream()).thenReturn(failingStdin);

		MlrBinder mlr = new MlrBinder(processBuilder);
		mlr.workingPath("workingPath");
		mlr.verb(new Verb("cat"));

		InputStreamReader isr = new InputStreamReader(
				new ByteArrayInputStream("x\n".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

		IOException ex = assertThrows(IOException.class, () -> mlr.run(isr));
		assertTrue(ex.getMessage().contains("pipe stdin"));
		verify(process).destroy();
	}

	@Test
	void runFail() throws IOException, InterruptedException {
		int exitCode = 2;
		String errMsg = "mlr: error";
		ProcessBuilder processBuilder = mock(ProcessBuilder.class);
		Process process = mock(Process.class);
		when(processBuilder.start()).thenReturn(process);
		when(process.waitFor()).thenReturn(exitCode);
		InputStream errStream = mock(InputStream.class);
		when(process.getErrorStream()).thenReturn(errStream);
		when(errStream.readAllBytes()).thenReturn(errMsg.getBytes(StandardCharsets.UTF_8));

		MlrBinder mlr = new MlrBinder(processBuilder);
		mlr.workingPath("workingPath");

		RuntimeException ex = assertThrows(RuntimeException.class, mlr::run);
		assertTrue(ex.getMessage().contains("failed"));
		assertTrue(ex.getMessage().contains(String.valueOf(exitCode)));
		assertTrue(ex.getMessage().contains(errMsg));
		verify(processBuilder).directory(any(File.class));
		verify(processBuilder).command(anyList());
	}

	@Test
	@DisplayName("mfrom expands to --mfrom paths then -- before verbs")
	void mfromPreVerbArgs() {
		MlrBinder mlr = new MlrBinder("mlr", "workingPath")
				.mfrom("a.csv", "b.csv")
				.verb(new Verb("cat"));
		assertEquals("mlr --mfrom a.csv b.csv -- cat", mlr.toString());
	}

	@Test
	@DisplayName("mload expands to --mload scripts then -- before verbs")
	void mloadPreVerbArgs() {
		MlrBinder mlr = new MlrBinder("mlr", "workingPath")
				.mload("one.mlr", "two.mlr")
				.verb(new Verb("cat"));
		assertEquals("mlr --mload one.mlr two.mlr -- cat", mlr.toString());
	}

	private ProcessBuilder getProcessBuilder(int exitCode, String runResult) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = mock(ProcessBuilder.class);
		Process process = mock(Process.class);
		when(processBuilder.start()).thenReturn(process);
		when(process.waitFor()).thenReturn(exitCode);
		InputStream is = mock(InputStream.class);
		when(process.getInputStream()).thenReturn(is);
		when(is.readAllBytes()).thenReturn(runResult.getBytes(StandardCharsets.UTF_8));
		return processBuilder;
	}
}
