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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.shed.mlrbinder.verb.Option;
import net.shed.mlrbinder.verb.Verb;

import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;

class MlrTest {
	private static Logger logger = Logger.getLogger(MlrTest.class.getName());

	@Test
	@DisplayName("mlrPath not nullable test")
	void mlrPathNotNullableTest() {
		Mlr mlr = Mlr.mlr();
		assertThrows(IllegalArgumentException.class, () -> {
			mlr.toString();
		});
	}

	@Test
	@DisplayName("mlrPath not nullable test")
	void workingPathNullableTest() {
		Mlr mlr = Mlr.binary("mlrPath");
		assertThrows(IllegalArgumentException.class, () -> {
			mlr.toString();
		});
	}

	@Test
	void pathTest() {
		String mlrPath = "mlr executable";
		logger.info("mlrPath=" + mlrPath);
		Mlr mlr = Mlr.mlr().mlrPath(mlrPath);
		logger.info(mlr.getMlrPath());
		assertEquals(mlrPath, mlr.getMlrPath());
	}

	@Test
	@DisplayName("added flags order")
	void flagsTest() {
		int cntOfFlags = new Random().nextInt(10);
		logger.info("cntOfFlags=" + cntOfFlags);
		Mlr mlr = Mlr.mlr();
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
		Mlr mlr = Mlr.inDir("workingPath").mlrPath("mlrPath");
		String toStringResult = "mlrPath";
		for (int i = 0; i < cntOfFlags; i++) {
			Flag flag = new Flag("flag" + i);
			mlr.flag(flag);
			toStringResult += Mlr.SPACER + flag;
		}
		logger.info("mlr.toString()=" + mlr.toString());
		assertEquals(mlr.toString(), toStringResult);
	}

	@Test
	@DisplayName("added verb order")
	void verbsTest() {
		int cntOfVerbs = new Random().nextInt(10);
		logger.info("cntOfVerbs=" + cntOfVerbs);
		Mlr mlr = Mlr.mlr();
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
		Mlr mlr = Mlr.mlr();
		List<Verb> verbs = new ArrayList<>();
		for (int i = 0; i < cntOfVerbs; i++) {
			verbs.add(new Verb("verb" + i));
		}
		mlr.verb(verbs.toArray(new Verb[0]));

		for (int i = 0; i < cntOfVerbs; i++) {
			assertEquals(verbs.get(i), mlr.getVerbs().get(i));
		}
	}

	@Test
	@DisplayName("added verbs then toString test")
	void verbsToStringTest() {
		int cntOfVerbs = new Random().nextInt(10);
		logger.info("cntOfVerbs=" + cntOfVerbs);
		Mlr mlr = Mlr.inDir("workingPath").mlrPath("mlrPath");
		String toStringResult = "mlrPath";
		for (int i = 0; i < cntOfVerbs; i++) {
			Verb verb = new Verb("verb" + i);
			mlr.verb(verb);
			if(i > 0) {
				toStringResult += Mlr.SPACER + Mlr.CHAINING_ADVERB;
			}
			toStringResult += Mlr.SPACER + verb;
		}
		logger.info("mlr.toString()=" + mlr.toString());
		assertEquals(mlr.toString(), toStringResult);
	}

	@Test
	@DisplayName("added files order")
	void filesTest() {
		int cntOfFiles = new Random().nextInt(10);
		logger.info("cntOfFiles=" + cntOfFiles);
		Mlr mlr = Mlr.mlr();
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
		Mlr mlr = Mlr.inDir("workingPath").mlrPath("mlrPath");
		String toStringResult = "mlrPath";
		for (int i = 0; i < cnt; i++) {
			String fileName = "fileName" + i;
			mlr.file(fileName);
			toStringResult += Mlr.SPACER + fileName;
		}
		logger.info("mlr.toString()=" + mlr.toString());
		assertEquals(toStringResult, mlr.toString());
	}

	@Test
	@DisplayName("added files, verbs and files then toString test")
	void flagsVerbsFilesToStringTest() {
		int cntOfFlags = new Random().nextInt(10);
		logger.info("cntOfFlags=" + cntOfFlags);
		Mlr mlr = Mlr.inDir("workingPath").mlrPath("mlrPath");
		String toStringResult = "mlrPath";
		for (int i = 0; i < cntOfFlags; i++) {
			Flag flag = new Flag("--flag" + i);
			flag.objective(new Objective("obj" + i));
			mlr.flag(flag);
			toStringResult += Mlr.SPACER + flag;
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
				toStringResult += Mlr.SPACER + Mlr.CHAINING_ADVERB;
			}
			toStringResult += Mlr.SPACER + verb;
		}

		int cnt = new Random().nextInt(10);
		logger.info("cnt=" + cnt);
		for (int i = 0; i < cnt; i++) {
			String fileName = "fileName" + i;
			mlr.file(fileName);
			toStringResult += Mlr.SPACER + fileName;
		}

		logger.info("mlr.toString()=" + mlr.toString());
		assertEquals(toStringResult, mlr.toString());
	}

	@Test
	void runSuccess() throws IOException, InterruptedException {
		int exitCode = 1;
		String runResult = "result";
		ProcessBuilder processBuilder = getProcessBuilder(exitCode, runResult);

		Mlr mlr = Mlr.withProcessBuilder(processBuilder);
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

		Mlr mlr = Mlr.withProcessBuilder(processBuilder);
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

		Mlr mlr = Mlr.withProcessBuilder(processBuilder);
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
		Mlr mlr = Mlr.inDir("workingPath")
				.mfrom("a.csv", "b.csv")
				.verb(new Verb("cat"));
		assertEquals("mlr --mfrom a.csv b.csv -- cat", mlr.toString());
	}

	@Test
	@DisplayName("mload expands to --mload scripts then -- before verbs")
	void mloadPreVerbArgs() {
		Mlr mlr = Mlr.inDir("workingPath")
				.mload("one.mlr", "two.mlr")
				.verb(new Verb("cat"));
		assertEquals("mlr --mload one.mlr two.mlr -- cat", mlr.toString());
	}

	@Test
	@DisplayName("fluent withCsvPreset().sort(n,nr).file matches manual assembly")
	void fluentCsvSortWithSortFlags() {
		Mlr mlr = Mlr.withCsvPreset()
				.workDir("workingPath")
				.sort(n("a"), nr("b"))
				.file("example.csv");
		assertEquals("mlr --csv sort -n a -nr b example.csv", mlr.toString());
	}

	@Test
	@DisplayName("file(File) absolute path sets working directory to parent")
	void fileAbsoluteSetsWorkingPathToParent() throws IOException {
		java.nio.file.Path dir = Files.createTempDirectory("mlrbinder-filetest");
		File csv = dir.resolve("x.csv").toFile();
		assertTrue(csv.createNewFile());
		csv.deleteOnExit();

		Mlr mlr = Mlr.mlr().file(csv);
		assertEquals(dir.toFile().getPath(), mlr.workingPath);
		assertEquals("mlr x.csv", mlr.toString());
	}

	@Test
	@DisplayName("file(File) relative path defaults working directory to user.dir")
	void fileRelativeDefaultsWorkingPathToUserDir() {
		Mlr mlr = Mlr.mlr().file(new File("sub/example.csv"));
		assertEquals(System.getProperty("user.dir"), mlr.workingPath);
		assertEquals("mlr sub/example.csv", mlr.toString());
	}

	@Test
	@DisplayName("workDir(File) sets working path from directory")
	void workDirFileSetsWorkingPath() throws IOException {
		java.nio.file.Path dir = Files.createTempDirectory("mlrbinder-workdir");
		Mlr mlr = Mlr.mlr().workDir(dir.toFile());
		assertEquals(dir.toFile().getPath(), mlr.workingPath);
	}

	@Test
	@DisplayName("getPreVerbArgs is unmodifiable")
	void getPreVerbArgsUnmodifiable() {
		Mlr mlr = Mlr.inDir("workingPath").mfrom("a.csv");
		assertThrows(UnsupportedOperationException.class, () -> mlr.getPreVerbArgs().add("x"));
	}

	@Test
	@DisplayName("mfrom and mload reject null collection")
	void mfromMloadRequireNonNull() {
		Mlr mlr = Mlr.inDir("workingPath");
		assertThrows(NullPointerException.class, () -> mlr.mfrom((String[]) null));
		assertThrows(NullPointerException.class, () -> mlr.mload((String[]) null));
		assertThrows(NullPointerException.class, () -> mlr.mfrom("ok", null));
		assertThrows(NullPointerException.class, () -> mlr.mload("ok", null));
	}

	@Test
	@DisplayName("run(InputStreamReader) rejects null reader")
	void runStdinRejectsNullReader() {
		Mlr mlr = Mlr.inDir("workingPath");
		assertThrows(IllegalArgumentException.class, () -> mlr.run((InputStreamReader) null));
	}

	@Test
	@DisplayName("run(InputStreamReader) requires working path")
	void runStdinRequiresWorkingPath() {
		Mlr mlr = Mlr.mlr();
		InputStreamReader isr = new InputStreamReader(
				new ByteArrayInputStream("x\n".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
		assertThrows(IllegalArgumentException.class, () -> mlr.run(isr));
	}

	@Test
	@DisplayName("redirectOutputFile getter returns set file")
	void redirectOutputFileGetter() throws IOException {
		File out = File.createTempFile("mlrbinder-out", ".csv");
		out.deleteOnExit();
		Mlr mlr = Mlr.inDir("workingPath").redirectOutputFile(out);
		assertEquals(out, mlr.getRedirectOutputFile());
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
