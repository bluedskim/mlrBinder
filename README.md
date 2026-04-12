# Miller Java Binder

A library that reduces friction when calling [Miller (`mlr`)](https://miller.readthedocs.io/) directly from Java.

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

How `mlr` invocations from [Miller in 10 minutes](https://miller.readthedocs.io/en/latest/10min/) map to this library. **All Java samples below use the recommended style:** start with **`Mlr.inDir(…)`** or **`Mlr.csv()`**, chain **global flags on `Mlr`**, and use **instance methods named like verbs** (`filter` / `split` → `.filterVerb` / `.splitVerb`). To add `--csv` only, use **`Mlr.inDir(…).csvFlag()`** (`Mlr.csv()` already applies the `mlr --csv` preset, so the name avoids clashing with the static factory). **Verb options** use `SortFlags` helpers `f` / `n` / `nr`, `import static …Flag.flag` with `flag("-f").objective("…")`, `option` / `objective`, and so on. Chaining several verbs automatically inserts `then` into the `run()` argv.

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
	.csvFlag()
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
Mlr.inDir(workingPath).csvFlag().cat().file("shape.csv").run();
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
	.csvFlag()
	.head(4)
	.file("example.csv")
	.run();
```

```bash
mlr --csv tail -n 4 example.csv
```

```java
Mlr.inDir(workingPath)
	.csvFlag()
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
Mlr.csv()
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
	.csvFlag()
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
	.csvFlag()
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
	.csvFlag()
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
	.csvFlag()
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
	.csvFlag()
	.sort(n("a"), nr("b"))
	.file("example.csv")
	.run();

// When starting from the CSV preset, use static Mlr.csv()
String runResult2 = Mlr.csv()
	.workDir(workingPath)
	.sort(n("a"), nr("b"))
	.file("example.csv")
	.run();
```

### `Mlr`: global-flag chain + verb-named instance methods (recommended)

- **`Mlr.csv()`** — static factory: starts with `mlr` + `--csv`. If `--csv` is already on the chain and you need another, use **`.csvFlag()`**.
- **Global-flag chain (recommended):** `.icsv()`, `.opprint()`, **`.csvFlag()`** (`--csv`; named so `csv` autocomplete groups sensibly), `.ocsv()`, `.ijson()`, **`.jsonFlag()`** (`--json`), `.idkvp()`, `.tsv()`, `.oxtab()`, `.ixtab()`, `.c2p()`, `.from("path")`, **`.inPlace()`** (`-I`), and so on mirror `flag(Flags…)` but **prefer the chain form**.
- **Verbs (recommended):** For each verb in `Verbs`, **`Mlr` exposes a same-named instance method** (for example `.uniq()`, `.histogram()`, `.join(…)`). Exceptions: **`filter`** → **`.filterVerb(…)`**, **`split`** → **`.splitVerb(…)`**. Convenience overloads: **`.head(n)`** / **`.tail(n)`**, **`.cutFields` / `.cutOrdered` / `.cutExcept`**, **`.stats1("count", "qty")`**, **`.splitBy("shape")`**, **`.putQuiet(…)`**. Use **`.verb(Mlr.Verbs.foo(…))`** only when the patterns above are awkward.

For `cut`’s `-o` / `-x` / `-f`, use **`CutFlags`** and **`.cutOrdered` / `.cutFields`**, or **`.cut(option(…), option(…))`**. `head`/`tail` counts: **`.head(4)`** / **`.tail(4)`** or **`HeadTail.n(4)`**. `stats1`’s `-a`/`-f`/`-g`: **`StatsFlags`**. `put -q`: **`.putQuiet(…)`**. `split -g`: **`.splitBy("shape")`**. Shared grouping: **`MillerVerbOpts.groupBy("field")`** (for example `head -g`).

For **`SortFlags`**, keep using **`n("field")` / `nr("field")` / `f("field")`** and **`n()`** (bare `-n`, for `head`).

```java
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;

String runResult = Mlr.csv()
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
	.csvFlag()
	.filterVerb(objective("$index > 1"))
	.file("example.csv")
	.run();
```

`file(File)` sets `workingPath` automatically when it is still unset: absolute file → parent directory; relative file → `user.dir`. Override anytime with `workDir(String)` or `workingPath(String)`.

## TODO

### v0.01

1. ~~logging~~
	1. ~~info to debug~~
1. ~~change to gradle library project~~
1. ~~add E2E test~~

### v0.02

1. ~~remove isConsecutive from the Verb~~
1. add static prebuilt object
	1. Verbs
	1. Flags

### v0.1

1. ~~execute mlr then connect output stream to isr~~ — stdin/stdout covered by `Mlr#run(InputStreamReader)` and `redirectOutputFile`
