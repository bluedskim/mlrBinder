# Miller Java Binder

Java에서 [Miller (`mlr`)](https://miller.readthedocs.io/)를 직접 호출할 때 생기는 불편을 줄이기 위한 라이브러리입니다.

## 목적

- **호출 방식**: Miller의 동작을 가능한 한 **Java 메서드·객체 조합**으로 표현합니다. 문자열로 긴 명령을 이어 붙이는 대신 `Flag`, `Verb`, `Option` 등으로 조립합니다.
- **컴파일 타임 검증**: API로 조립한 부분(플래그 이름, 동사 이름, 인자 순서 등)은 타입과 메서드 시그니처 수준에서 잡을 수 있습니다. 다만 Miller DSL(예: `put`의 표현식)이나 필드 이름의 유효성은 **런타임에 `mlr`이 검사**하므로, 그 부분까지 컴파일로 막을 수는 없습니다.
- **Miller 문법 몰라도 사용**: 자주 쓰는 입출력 형식은 `Flags`의 정적 팩토리로, 동사는 `Verb` / `Verbs`의 정적 팩토리로 노출되어 있어, Miller CLI 문서를 모두 외우지 않아도 Java 관용에 가깝게 쓸 수 있습니다. 세부 옵션은 여전히 `Flag`, `Objective`, `Option`으로 Miller와 동일한 인자를 넘깁니다.
- **실행**: 내부적으로는 구성한 인자 목록으로 `ProcessBuilder`가 `mlr` 프로세스를 띄웁니다. 시스템에 `mlr`이 설치되어 있고 `PATH`(또는 지정한 경로)에서 실행 가능해야 합니다.

## 범위와 한계

- **거의 모든 동사**: `Verbs` / `Verb`에 Miller 동사 이름에 대응하는 팩토리가 많이 포함되어 있습니다. **전역 플래그**는 upstream [reference-main-flag-list](https://github.com/johnkerl/miller/blob/main/docs/src/reference-main-flag-list.md)를 따라 `Flags`에 정적 팩토리로 두었으며, 갱신 시 `python3 utils/gen_flags.py`로 재생성합니다. 문서에 없는 플래그는 `Flags.raw("--name")` / `Flags.raw("--name", "value")` 또는 동사 옵션용 `Flag.flag("...")`로 넘깁니다. `--mfrom` / `--mload`처럼 인자 뒤에 `--`가 오는 형태는 `MlrBinder#mfrom` / `#mload`를 사용합니다.
- **네이티브 바인딩 아님**: Miller를 JVM 안에 링크한 것이 아니라 **외부 `mlr` 실행**입니다.
- **오류 시점**: 잘못된 조합의 일부는 `run()` / `run(InputStreamReader)` 시점에 exit code와 stderr로 드러납니다.

## Testing

```bash
./gradlew :lib:test
```

- 리포트: `lib/build/reports/tests/test/index.html`, 커버리지: `lib/build/jacocoHtml/index.html` ([Jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html))

## Miller 10분 튜토리얼 → Java로 옮기기

[Miller in 10 minutes](https://miller.readthedocs.io/en/latest/10min/)에 나오는 `mlr` 호출을 이 라이브러리로 표현할 때의 대응 관계입니다. **전역 플래그**는 `Flags`의 정적 메서드(아래에서는 `import static`으로 짧게 씀), **동사 옵션 토큰**은 `import static …Flag.flag` 후 `flag("-f").objective("…")`, `sort`의 `-f`/`-n`/`-nr` 필드는 `SortFlags`의 `f` / `n` / `nr`, **동사**는 `Verbs`의 정적 메서드, 그 밖의 값은 `option`·`objective`로 나눕니다. 여러 동사를 이어 쓰면 `run()` argv에 자동으로 `then`이 들어갑니다.

아래 Java 조각은 공통으로 다음 import를 둔다고 가정합니다(실제 코드에서는 필요한 것만 골라 써도 됩니다). `Option`은 `import static …Option.option` 후 `option(…)`으로 씁니다. `head` / `tail`의 `-n` 개수는 `SortFlags.n()`(인자 없음)과 `import static …Objective.objective` 후 `objective("4")`처럼 씁니다. `sort -n 필드`는 `n("필드")`처럼 **문자열 인자**가 있는 오버로드를 씁니다.

```java
import static net.shed.mlrbinder.Flags.c2p;
import static net.shed.mlrbinder.Flags.csv;
import static net.shed.mlrbinder.Flags.from;
import static net.shed.mlrbinder.Flags.icsv;
import static net.shed.mlrbinder.Flags.ijson;
import static net.shed.mlrbinder.Flags.inPlaceShort;
import static net.shed.mlrbinder.Flags.ocsv;
import static net.shed.mlrbinder.Flags.opprint;
import static net.shed.mlrbinder.Flag.flag;
import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.f;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Verbs.cat;
import static net.shed.mlrbinder.verb.Verbs.cut;
import static net.shed.mlrbinder.verb.Verbs.filter;
import static net.shed.mlrbinder.verb.Verbs.head;
import static net.shed.mlrbinder.verb.Verbs.put;
import static net.shed.mlrbinder.verb.Verbs.sort;
import static net.shed.mlrbinder.verb.Verbs.stats1;
import static net.shed.mlrbinder.verb.Verbs.tail;
import static net.shed.mlrbinder.verb.Option.option;

import net.shed.mlrbinder.MlrBinder;
```

더 많은 패턴은 `TenMinTutorialE2eTest`와 `lib/src/test/resources/10min/`을 참고하면 됩니다.

### 입출력 플래그와 `cat`

```bash
mlr --csv cat example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(csv())
	.verb(cat())
	.file("example.csv")
	.run();
```

```bash
mlr --icsv --opprint cat example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(icsv())
	.flag(opprint())
	.verb(cat())
	.file("example.csv")
	.run();
```

### `head` / `tail` 옵션

```bash
mlr --csv head -n 4 example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(csv())
	.verb(head(option(n(), objective("4"))))
	.file("example.csv")
	.run();
```

```bash
mlr --csv tail -n 4 example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(csv())
	.verb(tail(option(n(), objective("4"))))
	.file("example.csv")
	.run();
```

### `sort`, `cut`

```bash
mlr --icsv --opprint sort -f shape -nr index example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(icsv())
	.flag(opprint())
	.verb(sort(f("shape"), nr("index")))
	.file("example.csv")
	.run();
```

```bash
mlr --icsv --opprint cut -o -f flag,shape example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(icsv())
	.flag(opprint())
	.verb(cut(
		option(flag("-o")),
		option(flag("-f").objective("flag,shape"))))
	.file("example.csv")
	.run();
```

### `filter` / `put` (DSL은 문자열 그대로)

```bash
mlr --icsv --opprint filter '$color == "red"' example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(icsv())
	.flag(opprint())
	.verb(filter(objective("$color == \"red\"")))
	.file("example.csv")
	.run();
```

```bash
mlr --icsv --opprint put '$[[3]] = "NEW"' example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(icsv())
	.flag(opprint())
	.verb(put(objective("$[[3]] = \"NEW\"")))
	.file("example.csv")
	.run();
```

### 필드 이름에 공백 (`-nr` 값은 셸 따옴표 없이 한 토큰)

```bash
mlr --csv cat spaces.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(csv())
	.verb(cat())
	.file("spaces.csv")
	.run();
```

```bash
mlr --c2p sort -nr 'Total MWh' spaces.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(c2p())
	.verb(sort(nr("Total MWh")))
	.file("spaces.csv")
	.run();
```

```bash
mlr --c2p put '${Total KWh} = ${Total MWh} * 1000' spaces.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(c2p())
	.verb(put(objective("${Total KWh} = ${Total MWh} * 1000")))
	.file("spaces.csv")
	.run();
```

### 여러 입력 파일

```bash
mlr --csv cat data/a.csv data/b.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(csv())
	.verb(cat())
	.file("data/a.csv")
	.file("data/b.csv")
	.run();
```

### `then`으로 동사 연결

```bash
mlr --icsv --opprint sort -nr index then head -n 3 example.csv
```

```java
new MlrBinder("mlr", workingPath)
	.flag(icsv())
	.flag(opprint())
	.verb(
		sort(nr("index")),
		head(option(n(), objective("3"))))
	.file("example.csv")
	.run();
```

튜토리얼의 **셸 파이프** (`mlr … | mlr …`)는 Java에서 `MlrBinder`를 두 번 쓰면 됩니다. 첫 단계 stdout을 임시 파일로내려면 `redirectOutputFile`을 쓰고, 두 번째는 그 디렉터리에서 stdin 없이 파일만 넘깁니다.

```bash
mlr --csv sort -nr index example.csv | mlr --icsv --opprint head -n 3
```

```java
import java.nio.file.Files;
import java.nio.file.Path;

Path tmp = Files.createTempDirectory("mlr-pipe");
Path sorted = tmp.resolve("sorted.csv");
new MlrBinder("mlr", workingPath)
	.flag(csv())
	.verb(sort(nr("index")))
	.file("example.csv")
	.redirectOutputFile(sorted.toFile())
	.run();

String pprintTop = new MlrBinder("mlr", tmp.toString())
	.flag(icsv())
	.flag(opprint())
	.verb(head(option(n(), objective("3"))))
	.file(sorted.getFileName().toString())
	.run();
```

### `--from`

```bash
mlr --icsv --opprint --from example.csv sort -nr index then head -n 3
```

```java
new MlrBinder("mlr", workingPath)
	.flag(icsv())
	.flag(opprint())
	.flag(from("example.csv"))
	.verb(
		sort(nr("index")),
		head(option(n(), objective("3"))))
	.run();
```

### `--mfrom` / `--mload` (가변 인자 뒤 `--`)

```bash
mlr --csv --mfrom a.csv b.csv -- cat
```

```java
new MlrBinder("mlr", workingPath)
	.flag(csv())
	.mfrom("a.csv", "b.csv")
	.verb(cat())
	.run();
```

### `stats1` 등 옵션이 많은 동사

```bash
mlr --icsv --opprint --from example.csv stats1 -a count,min,mean,max -f quantity -g shape
```

```java
new MlrBinder("mlr", workingPath)
	.flag(icsv())
	.flag(opprint())
	.flag(from("example.csv"))
	.verb(stats1(
		flag("-a").objective("count,min,mean,max"),
		flag("-f").objective("quantity"),
		flag("-g").objective("shape")))
	.run();
```

### JSON 입출력

```bash
mlr --ijson --ocsv cat example.json
```

```java
new MlrBinder("mlr", workingPath)
	.flag(ijson())
	.flag(ocsv())
	.verb(cat())
	.file("example.json")
	.run();
```

### 제자리 수정 `-I`

```bash
mlr -I --csv sort -f shape newfile.txt
```

```java
new MlrBinder("mlr", tmpDir)
	.inPlace()
	.flag(csv())
	.sort(f("shape"))
	.file("newfile.txt")
	.run();
```

튜토리얼의 `put` 예시에 나오는 `$y2`는 문서 오타에 가깝고, Miller에서는 제곱에 `$y**2`를 씁니다.

## Examples

```java
import static net.shed.mlrbinder.Flags.csv;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Verbs.sort;

import net.shed.mlrbinder.MlrBinder;

// 저수준 조립: sort 키는 SortFlags, 동사는 Verbs
String runResult = new MlrBinder("mlr", workingPath)
	.workingPath(workingPath)
	.flag(csv())
	.verb(
		sort()
			.addArg(n("a"))
			.addArg(nr("b"))
	)
	.file("example.csv")
	.run();

// 동사에 sort 키를 가변 인자로
String runResult2 = new MlrBinder("mlr", workingPath)
	.workingPath(workingPath)
	.flag(csv())
	.verb(sort(n("a"), nr("b")))
	.file("example.csv")
	.run();
```

### Fluent entry (`MlrBinder` + small flag helpers)

- **`MlrBinder.csv()`** — `mlr` + `--csv`로 시작.
- **전역 플래그 체인**: `.icsv()`, `.opprint()`, `.ocsv()`, `.ijson()`, `.json()`, `.oxtab()`, `.ixtab()`, `.c2p()`, `.from("path")`, **`.inPlace()`** (`-I`) 등은 각각 `flag(Flags…)`와 동일합니다.
- **동사 체인**: `.sort(…)`, `.cat(…)`, `.head(n)`, `.tail(n)`, `.filter(objective("…"))`, `.put(…)`, `.putQuiet(…)`, `.cutFields("a,b")`, `.cutOrdered("a,b")`, `.cutExcept("a,b")`, `.stats1(aggregations, field)`, `.stats1(aggregations, field, groupBy)`, `.splitBy("field")`.

`cut`의 `-o` / `-x` / `-f`는 **`CutFlags.o()`**, **`CutFlags.x()`**, **`CutFlags.f("…")`** (또는 위 `cut*` 메서드). `head`/`tail` 개수는 **`HeadTail.n(4)`** (= `option(SortFlags.n(), objective("4"))`). `stats1`의 `-a`/`-f`/`-g`는 **`StatsFlags`**. `put -q`는 **`PutFlags.quiet()`** 또는 **`.putQuiet(…)`**. `split -g`는 **`SplitFlags.group("shape")`** 또는 **`.splitBy("shape")`**. 여러 동사에 공통인 **`MillerVerbOpts.groupBy("field")`** (`head -g` 등)도 씁니다.

`SortFlags`의 **`n("field")` / `nr("field")` / `f("field")`** 와 **`n()`** (값 없는 `-n`, `head`용)은 그대로 씁니다.

```java
import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.f;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;

String runResult = MlrBinder.csv()
	.workDir(workingPath)
	.sort(n("a"), nr("b"))
	.file(new File("example.csv"))
	.run();

// 동일 튜토리얼 흐름을 더 짧게
String top3 = new MlrBinder("mlr", workingPath)
	.icsv()
	.opprint()
	.sort(nr("index"))
	.head(3)
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

1. ~~execute mlr then connect output stream to isr~~ — `MlrBinder#run(InputStreamReader)` 및 `redirectOutputFile`로 stdin/stdout 처리 가능
