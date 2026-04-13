# mlr-binder

**Turn [Miller](https://miller.readthedocs.io/) into a fluent Java API**—shape CSV, JSON, TSV, and DKVP in batch jobs, services, and tests without hand-built shell strings or copy-pasted argv lists. **Miller Java Binder** wraps the real `mlr` process so you keep upstream Miller semantics while your JVM code stays readable and refactor-friendly.

## Why?

- **Less string soup:** Build invocations with `Mlr` chains, `Flag`, `Verb`, and `Option` instead of concatenating commands you cannot safely rename or review in the IDE.
- **Catch mistakes earlier:** Method names and types encode Miller’s structure; only Miller DSL snippets (for example `put` / `filter` expressions) are still validated when `mlr` runs.
- **Fast integration:** One Maven or Gradle dependency, `mlr` on `PATH` (or a path you configure), then `Mlr.inDir(...).csv().sort(...).file(...).run()`—no native JNI layer or extra services.
- **Maven Central:** Published as **`io.github.bluedskim:mlr-binder`** (coordinates match [https://repo1.maven.org/maven2/io/github/bluedskim/mlr-binder/0.2/](https://repo1.maven.org/maven2/io/github/bluedskim/mlr-binder/0.2/)).

## Install

**Requires Java 11+.** Package: **`net.shed.mlrbinder`**.

Maven (`pom.xml`):

```xml
<dependency>
  <groupId>io.github.bluedskim</groupId>
  <artifactId>mlr-binder</artifactId>
  <version>0.2</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
dependencies {
    implementation("io.github.bluedskim:mlr-binder:0.2")
}
```

Gradle (Groovy):

```groovy
dependencies {
    implementation 'io.github.bluedskim:mlr-binder:0.2'
}
```

## Use cases

- **Data prep and ETL:** Sort, cut, join, reshape, and aggregate large delimited or JSON files inside JVM pipelines while Miller stays the engine.
- **Backend and batch jobs:** Run repeatable `mlr` workflows from schedulers or workers with working-directory and file helpers instead of brittle `Runtime.exec` strings.
- **Quality gates and fixtures:** Mirror [Miller in 10 minutes](https://miller.readthedocs.io/en/latest/10min/)-style flows in tests (see the tutorial mapping section below) to lock behavior to known Miller releases.
- **Polyglot teams:** Let analysts keep Miller expertise; let application teams call the same verbs from Java with a single shared dependency.

---

## Supported Miller (`mlr`) version

This library is developed and tested against **Miller [`mlr` 6.17.0](https://github.com/johnkerl/miller/releases/tag/v6.17.0)**. Install that version on your `PATH` (or confirm behavior yourself if you use a different release).

**Binder-only “sugar” on `Mlr`:** Some `Mlr` methods bundle common Miller verb options into one call (for example `uniqCountBy` → `uniq -c -g …`). These Java names are **not** Miller CLI features; the child process is always standard `mlr`. Each sugar method documents the **Miller CLI equivalent** in its Javadoc, and the `net.shed.mlrbinder` package summary lists them in one table (see generated Javadoc or your IDE’s quick documentation for `Mlr` / the package).

**Recommended style:** Prefer a single `Mlr` chain: use **`Mlr`’s global-flag chain methods** (`.icsv()`, `.from("…")`, and so on) for global flags, and **instance methods named like Miller verbs** (`.sort(…)`, `.cat()`, and so on; only `filter` / `split` use `.filterVerb()` / `.splitVerb()`). Use the `flag(Flags…)` + `verb(Mlr.Verbs…)` combination only when you need it.

## Goals

- **How you invoke Miller:** Express Miller behavior as much as possible with **Java methods and objects** instead of concatenating long command strings; assemble with `Flag`, `Verb`, `Option`, and related types.
- **Compile-time checks:** Parts you build through the API (flag names, verb names, argument order, and so on) can be caught at the type and method-signature level. Miller DSL (for example `put` expressions) and field-name validity are still **checked at runtime by `mlr`**, so the compiler cannot catch everything.
- **Usable without deep Miller syntax knowledge:** Following the **recommended style** above, global flags go on the **`Mlr` chain**, verbs use **`Mlr` methods with the same names as verbs** (exceptions: `filter` → `.filterVerb()`, `split` → `.splitVerb()`). There is also a **`Mlr.Verbs`** static factory for internals and advanced assembly. Pass the same arguments as Miller for fine-grained options via `Flag`, `Objective`, and `Option`. (`Verb` is just an argv fragment; avoid `new Verb(...)` in application code when you can.)
- **Execution:** Internally, `ProcessBuilder` starts an `mlr` process with the assembled argument list. `mlr` must be installed and executable on `PATH` (or the path you configure).

## Scope and limitations

- **Nearly all verbs:** Factories per Miller verb live in `net.shed.mlrbinder.verb.Verbs`, and both **`Mlr` verb-named instance methods** and **`Mlr.Verbs`** delegate there. **Global flags** follow upstream [reference-main-flag-list](https://github.com/johnkerl/miller/blob/main/docs/src/reference-main-flag-list.md) as static factories on `Flags`, and **`Mlr` exposes matching chain methods** (regenerate with `python3 utils/gen_flags.py`). Flags missing from the docs can be passed with `Flags.raw("--name")` / `Flags.raw("--name", "value")` or verb options via `Flag.flag("...")`. For forms like `--mfrom` / `--mload` where `--` follows variadic arguments, use `Mlr#mfrom` / `#mload`.
- **Not a native binding:** This **runs external `mlr`**, not Miller linked inside the JVM.
- **When errors surface:** Some invalid combinations appear at `run()` / `run(InputStreamReader)` via exit code and stderr.

## Testing

```bash
./gradlew :lib:test
```

- Reports: `lib/build/reports/tests/test/index.html`, coverage: `lib/build/jacocoHtml/index.html` ([Jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html))

## Miller in 10 minutes → Java

How `mlr` invocations from [Miller in 10 minutes](https://miller.readthedocs.io/en/latest/10min/) map to this library. **All Java samples below use the recommended style:** start with **`Mlr.inDir(…)`** (and then **`.csv()`** when you need `--csv`), or start from **`Mlr.withCsvPreset()`** (static: `mlr` + `--csv` before you set `workDir` / files). Chain **global flags on `Mlr`**, and use **instance methods named like verbs** (`filter` / `split` → `.filterVerb` / `.splitVerb`). To append another `--csv` on the same chain, call **`.csv()`** again. **Verb options** use `SortFlags` helpers `f` / `n` / `nr`, `import static …Flag.flag` with `flag("-f").objective("…")`, `option` / `objective`, and so on. Chaining several verbs automatically inserts `then` into the `run()` argv.

The Java snippets below assume the following imports in common (pick only what you need in real code):

```java
import static net.shed.mlrbinder.Flag.flag;
import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.f;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Option.option;

import net.shed.mlrbinder.Mlr;
```

For more patterns, see `TenMinTutorialE2eTest`, `TenMinTutorialFormatsE2eTest` (the tutorial’s CSV/JSON/DKVP/TSV file-format sections), and `lib/src/test/resources/10min/`.

### I/O flags and `cat`

```bash
mlr --csv cat example.csv
```

```java
Mlr.inDir(workingPath)
	.csv()
	.cat()
	.file("example.csv")
	.run();
```

```bash
mlr --icsv --opprint cat example.csv
```

```java
Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.cat()
	.file("example.csv")
	.run();
```

### File formats (CSV, JSON, DKVP, TSV)

As in the tutorial’s “File formats” section, set input format with `--csv` / `--json` / `--idkvp` / `--tsv`, and so on.

```bash
mlr --csv cat shape.csv
mlr --json cat shape.json
mlr --idkvp --ocsv cat shape.dkvp
mlr --tsv cat shape.tsv
```

```java
Mlr.inDir(workingPath).csv().cat().file("shape.csv").run();
Mlr.inDir(workingPath).jsonFlag().cat().file("shape.json").run();
Mlr.inDir(workingPath).idkvp().ocsv().cat().file("shape.dkvp").run();
Mlr.inDir(workingPath).tsv().cat().file("shape.tsv").run();
```

### `head` / `tail` options

```bash
mlr --csv head -n 4 example.csv
```

```java
Mlr.inDir(workingPath)
	.csv()
	.head(4)
	.file("example.csv")
	.run();
```

```bash
mlr --csv tail -n 4 example.csv
```

```java
Mlr.inDir(workingPath)
	.csv()
	.tail(4)
	.file("example.csv")
	.run();
```

### `sort`, `cut`

```bash
mlr --icsv --opprint sort -f shape -nr index example.csv
```

```java
Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.sort(f("shape"), nr("index"))
	.file("example.csv")
	.run();
```

```bash
mlr --icsv --opprint cut -o -f flag,shape example.csv
```

```java
Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.cut(
		option(flag("-o")),
		option(flag("-f").objective("flag,shape")))
	.file("example.csv")
	.run();
```

### `filter` / `put` (DSL stays a plain string)

```bash
mlr --icsv --opprint filter '$color == "red"' example.csv
```

```java
Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.filterVerb(objective("$color == \"red\""))
	.file("example.csv")
	.run();
```

```bash
mlr --icsv --opprint put '$[[3]] = "NEW"' example.csv
```

```java
Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.put(objective("$[[3]] = \"NEW\""))
	.file("example.csv")
	.run();
```

### Field names with spaces (`-nr` value is one token without shell quotes)

```bash
mlr --csv cat spaces.csv
```

```java
Mlr.withCsvPreset()
	.workDir(workingPath)
	.cat()
	.file("spaces.csv")
	.run();
```

```bash
mlr --c2p sort -nr 'Total MWh' spaces.csv
```

```java
Mlr.inDir(workingPath)
	.c2p()
	.sort(nr("Total MWh"))
	.file("spaces.csv")
	.run();
```

```bash
mlr --c2p put '${Total KWh} = ${Total MWh} * 1000' spaces.csv
```

```java
Mlr.inDir(workingPath)
	.c2p()
	.put(objective("${Total KWh} = ${Total MWh} * 1000"))
	.file("spaces.csv")
	.run();
```

### Multiple input files

```bash
mlr --csv cat data/a.csv data/b.csv
```

```java
Mlr.inDir(workingPath)
	.csv()
	.cat()
	.file("data/a.csv")
	.file("data/b.csv")
	.run();
```

### Chaining verbs with `then`

```bash
mlr --icsv --opprint sort -nr index then head -n 3 example.csv
```

```java
Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.sort(nr("index"))
	.head(3)
	.file("example.csv")
	.run();
```

The tutorial’s **shell pipe** (`mlr … | mlr …`) maps to two `Mlr` calls in Java. To capture the first stage’s stdout to a temp file, use `redirectOutputFile`; the second stage runs in that directory with files only (no stdin).

```bash
mlr --csv sort -nr index example.csv | mlr --icsv --opprint head -n 3
```

```java
import java.nio.file.Files;
import java.nio.file.Path;

Path tmp = Files.createTempDirectory("mlr-pipe");
Path sorted = tmp.resolve("sorted.csv");
Mlr.inDir(workingPath)
	.csv()
	.sort(nr("index"))
	.file("example.csv")
	.redirectOutputFile(sorted.toFile())
	.run();

String pprintTop = Mlr.inDir(tmp.toString())
	.icsv()
	.opprint()
	.head(3)
	.file(sorted.getFileName().toString())
	.run();
```

### `--from`

```bash
mlr --icsv --opprint --from example.csv sort -nr index then head -n 3
```

```java
Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.from("example.csv")
	.sort(nr("index"))
	.head(3)
	.run();
```

### `--mfrom` / `--mload` (`--` after variadic arguments)

```bash
mlr --csv --mfrom a.csv b.csv -- cat
```

```java
Mlr.inDir(workingPath)
	.csv()
	.mfrom("a.csv", "b.csv")
	.cat()
	.run();
```

### Verbs with many options such as `stats1`

```bash
mlr --icsv --opprint --from example.csv stats1 -a count,min,mean,max -f quantity -g shape
```

```java
Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.from("example.csv")
	.stats1(
		flag("-a").objective("count,min,mean,max"),
		flag("-f").objective("quantity"),
		flag("-g").objective("shape"))
	.run();
```

### JSON input and output

```bash
mlr --ijson --ocsv cat example.json
```

```java
Mlr.inDir(workingPath)
	.ijson()
	.ocsv()
	.cat()
	.file("example.json")
	.run();
```

### In-place update `-I`

```bash
mlr -I --csv sort -f shape newfile.txt
```

```java
Mlr.inDir(tmpDir)
	.inPlace()
	.csv()
	.sort(f("shape"))
	.file("newfile.txt")
	.run();
```

The tutorial’s `put` example mentioning `$y2` is likely a documentation typo; in Miller you write squaring as `$y**2`.

## Examples

```java
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;

import net.shed.mlrbinder.Mlr;

// Recommended: global-flag chain + verb-named methods
String runResult = Mlr.inDir(workingPath)
	.csv()
	.sort(n("a"), nr("b"))
	.file("example.csv")
	.run();

// When starting from the CSV preset, use static Mlr.withCsvPreset()
String runResult2 = Mlr.withCsvPreset()
	.workDir(workingPath)
	.sort(n("a"), nr("b"))
	.file("example.csv")
	.run();
```

### `Mlr`: global-flag chain + verb-named instance methods (recommended)

- **`Mlr.withCsvPreset()`** — static entry: `mlr` + `--csv` (no working directory yet; then `.workDir(…)` / `.file(…)`). For a second `--csv` on the same chain, call **`.csv()`** again.
- **Global-flag chain (recommended):** `.icsv()`, `.opprint()`, **`.csv()`** (appends `--csv`), `.ocsv()`, `.ijson()`, **`.jsonFlag()`** (`--json`), `.idkvp()`, `.tsv()`, `.oxtab()`, `.ixtab()`, `.c2p()`, `.from("path")`, **`.inPlace()`** (`-I`), and so on mirror `flag(Flags…)` but **prefer the chain form**.
- **Verbs (recommended):** For each verb in `Verbs`, **`Mlr` exposes a same-named instance method** (for example `.uniq()`, `.histogram()`, `.join(…)`). Exceptions: **`filter`** → **`.filterVerb(…)`**, **`split`** → **`.splitVerb(…)`**. Convenience overloads: **`.head(n)`** / **`.tail(n)`**, **`.cutFields` / `.cutOrdered` / `.cutExcept`**, **`.stats1("count", "qty")`**, **`.splitBy("shape")`**, **`.putQuiet(…)`**. Use **`.verb(Mlr.Verbs.foo(…))`** only when the patterns above are awkward.

For `cut`’s `-o` / `-x` / `-f`, use **`CutFlags`** and **`.cutOrdered` / `.cutFields`**, or **`.cut(option(…), option(…))`**. `head`/`tail` counts: **`.head(4)`** / **`.tail(4)`** or **`HeadTail.n(4)`**. `stats1`’s `-a`/`-f`/`-g`: **`StatsFlags`**. `put -q`: **`.putQuiet(…)`**. `split -g`: **`.splitBy("shape")`**. Shared grouping: **`MillerVerbOpts.groupBy("field")`** (for example `head -g`).

For **`SortFlags`**, keep using **`n("field")` / `nr("field")` / `f("field")`** and **`n()`** (bare `-n`, for `head`).

```java
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;

String runResult = Mlr.withCsvPreset()
	.workDir(workingPath)
	.sort(n("a"), nr("b"))
	.file(new File("example.csv"))
	.run();

// Same tutorial flow
String top3 = Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.sort(nr("index"))
	.head(3)
	.file("example.csv")
	.run();
```

Miller `filter` / `split` verbs are invoked on the chain as **`.filterVerb(…)`** and **`.splitVerb(…)`**.

```java
import static net.shed.mlrbinder.Objective.objective;

Mlr.inDir(workingPath)
	.csv()
	.filterVerb(objective("$index > 1"))
	.file("example.csv")
	.run();
```

`file(File)` sets `workingPath` automatically when it is still unset: absolute file → parent directory; relative file → `user.dir`. Override anytime with `workDir(String)` or `workingPath(String)`.
