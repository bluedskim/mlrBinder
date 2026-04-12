# Miller Java Binder

Java에서 [Miller (`mlr`)](https://miller.readthedocs.io/)를 직접 호출할 때 생기는 불편을 줄이기 위한 라이브러리입니다.

**권장 스타일:** `Mlr` 하나로 이어 쓰되, **전역 플래그는 `Mlr`의 전역 플래그 체인 메서드**(`.icsv()`, `.from("…")` 등), **Miller 동사는 `verb`와 같은 이름의 인스턴스 메서드**(`.sort(…)`, `.cat()` 등; `filter` / `split`만 `.filterVerb()` / `.splitVerb()`)를 쓰는 것을 **강력히 권장**합니다. `flag(Flags…)` + `verb(Mlr.Verbs…)` 조합은 필요할 때만 쓰면 됩니다.

## 목적

- **호출 방식**: Miller의 동작을 가능한 한 **Java 메서드·객체 조합**으로 표현합니다. 문자열로 긴 명령을 이어 붙이는 대신 `Flag`, `Verb`, `Option` 등으로 조립합니다.
- **컴파일 타임 검증**: API로 조립한 부분(플래그 이름, 동사 이름, 인자 순서 등)은 타입과 메서드 시그니처 수준에서 잡을 수 있습니다. 다만 Miller DSL(예: `put`의 표현식)이나 필드 이름의 유효성은 **런타임에 `mlr`이 검사**하므로, 그 부분까지 컴파일로 막을 수는 없습니다.
- **Miller 문법 몰라도 사용**: 위 **권장 스타일**대로, 전역 플래그는 **`Mlr` 체인**, 동사는 **`verb`와 동일한 이름의 `Mlr` 메서드**(예외: `filter`→`.filterVerb()`, `split`→`.splitVerb()`). 내부 구현·고급 조립용으로 **`Mlr.Verbs`** 정적 팩토리도 있습니다. 세부 옵션은 `Flag`, `Objective`, `Option`으로 Miller와 동일한 인자를 넘깁니다. (`Verb` 타입은 argv 조각일 뿐, 사용자 코드에서 `new Verb(...)`는 피하는 것이 좋습니다.)
- **실행**: 내부적으로는 구성한 인자 목록으로 `ProcessBuilder`가 `mlr` 프로세스를 띄웁니다. 시스템에 `mlr`이 설치되어 있고 `PATH`(또는 지정한 경로)에서 실행 가능해야 합니다.

## 범위와 한계

- **거의 모든 동사**: 내부 `net.shed.mlrbinder.verb.Verbs`에 Miller 동사별 팩토리가 정의되어 있고, **`Mlr`의 동사 이름 인스턴스 메서드**와 **`Mlr.Verbs`**가 모두 여기로 위임됩니다. **전역 플래그**는 upstream [reference-main-flag-list](https://github.com/johnkerl/miller/blob/main/docs/src/reference-main-flag-list.md)를 따라 `Flags`에 정적 팩토리로 두었으며, **`Mlr`에도 같은 플래그를 붙이는 체인 메서드**가 있습니다(갱신은 `python3 utils/gen_flags.py`). 문서에 없는 플래그는 `Flags.raw("--name")` / `Flags.raw("--name", "value")` 또는 동사 옵션용 `Flag.flag("...")`로 넘깁니다. `--mfrom` / `--mload`처럼 인자 뒤에 `--`가 오는 형태는 `Mlr#mfrom` / `#mload`를 사용합니다.
- **네이티브 바인딩 아님**: Miller를 JVM 안에 링크한 것이 아니라 **외부 `mlr` 실행**입니다.
- **오류 시점**: 잘못된 조합의 일부는 `run()` / `run(InputStreamReader)` 시점에 exit code와 stderr로 드러납니다.

## Testing

```bash
./gradlew :lib:test
```

- 리포트: `lib/build/reports/tests/test/index.html`, 커버리지: `lib/build/jacocoHtml/index.html` ([Jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html))

## Miller 10분 튜토리얼 → Java로 옮기기

[Miller in 10 minutes](https://miller.readthedocs.io/en/latest/10min/)에 나오는 `mlr` 호출을 이 라이브러리로 표현할 때의 대응 관계입니다. **아래 Java 샘플은 모두 권장 스타일**입니다: **`Mlr.inDir(…)`** 또는 **`Mlr.csv()`**로 시작하고, **전역 플래그는 `Mlr` 체인 메서드**, **동사는 `verb`와 같은 이름의 인스턴스 메서드**로 이어 씁니다(`filter` / `split` → `.filterVerb` / `.splitVerb`). `--csv`만 더할 때는 **`Mlr.inDir(…).csvFlag()`** (`Mlr.csv()`는 이미 `mlr --csv` 프리셋이라 정적 팩토리 이름과 겹침을 피함). **동사 옵션**은 `SortFlags`의 `f` / `n` / `nr`, `import static …Flag.flag` 후 `flag("-f").objective("…")`, `option`·`objective` 등으로 나눕니다. 여러 동사를 이어 쓰면 `run()` argv에 자동으로 `then`이 들어갑니다.

아래 Java 조각은 공통으로 다음 import를 둔다고 가정합니다(실제 코드에서는 필요한 것만 골라 써도 됩니다).

```java
import static net.shed.mlrbinder.Flag.flag;
import static net.shed.mlrbinder.Objective.objective;
import static net.shed.mlrbinder.SortFlags.f;
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;
import static net.shed.mlrbinder.verb.Option.option;

import net.shed.mlrbinder.Mlr;
```

더 많은 패턴은 `TenMinTutorialE2eTest`, `TenMinTutorialFormatsE2eTest`(튜토리얼의 CSV/JSON/DKVP/TSV 등 파일 형식 절), `lib/src/test/resources/10min/`을 참고하면 됩니다.

### 입출력 플래그와 `cat`

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

### 파일 형식 (CSV, JSON, DKVP, TSV)

튜토리얼의 “File formats” 절과 같이, 입력 형식은 `--csv` / `--json` / `--idkvp` / `--tsv` 등으로 지정합니다.

```bash
mlr --csv cat shape.csv
mlr --json cat shape.json
mlr --idkvp --ocsv cat shape.dkvp
mlr --tsv cat shape.tsv
```

```java
Mlr.inDir(workingPath).csvFlag().cat().file("shape.csv").run();
Mlr.inDir(workingPath).json().cat().file("shape.json").run();
Mlr.inDir(workingPath).idkvp().ocsv().cat().file("shape.dkvp").run();
Mlr.inDir(workingPath).tsv().cat().file("shape.tsv").run();
```

### `head` / `tail` 옵션

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

### `filter` / `put` (DSL은 문자열 그대로)

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

### 필드 이름에 공백 (`-nr` 값은 셸 따옴표 없이 한 토큰)

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

### 여러 입력 파일

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

### `then`으로 동사 연결

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

튜토리얼의 **셸 파이프** (`mlr … | mlr …`)는 Java에서 `Mlr`를 두 번 쓰면 됩니다. 첫 단계 stdout을 임시 파일로내려면 `redirectOutputFile`을 쓰고, 두 번째는 그 디렉터리에서 stdin 없이 파일만 넘깁니다.

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

### `--mfrom` / `--mload` (가변 인자 뒤 `--`)

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

### `stats1` 등 옵션이 많은 동사

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

### JSON 입출력

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

### 제자리 수정 `-I`

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

튜토리얼의 `put` 예시에 나오는 `$y2`는 문서 오타에 가깝고, Miller에서는 제곱에 `$y**2`를 씁니다.

## Examples

```java
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;

import net.shed.mlrbinder.Mlr;

// 권장: 전역 플래그 체인 + 동사 이름 메서드
String runResult = Mlr.inDir(workingPath)
	.csvFlag()
	.sort(n("a"), nr("b"))
	.file("example.csv")
	.run();

// CSV 프리셋으로 시작할 때는 정적 Mlr.csv()
String runResult2 = Mlr.csv()
	.workDir(workingPath)
	.sort(n("a"), nr("b"))
	.file("example.csv")
	.run();
```

### `Mlr`: 전역 플래그 체인 + 동사 이름 인스턴스 메서드 (권장)

- **`Mlr.csv()`** — 정적 팩토리: `mlr` + `--csv`로 시작합니다. 이미 `--csv`가 붙은 상태에서 같은 체인을 이어 쓸 때는 **`.csvFlag()`**로 또 `--csv`를 붙일 수 있습니다.
- **전역 플래그 체인 (권장)**: `.icsv()`, `.opprint()`, **`.csvFlag()`** (`--csv`; `csv`로 자동완성 검색 시 묶이도록 이름 붙임), `.ocsv()`, `.ijson()`, `.json()`, `.idkvp()`, `.tsv()`, `.oxtab()`, `.ixtab()`, `.c2p()`, `.from("path")`, **`.inPlace()`** (`-I`) 등은 각각 `flag(Flags…)`와 동일하지만, **체인 형태를 우선** 쓰세요.
- **동사 (권장)**: `Verbs`에 정의된 Miller 동사마다 **`Mlr`에 같은 이름의 인스턴스 메서드**가 있습니다(예: `.uniq()`, `.histogram()`, `.join(…)`). 예외: **`filter`** → **`.filterVerb(…)`**, **`split`** → **`.splitVerb(…)`**. 편의 오버로드: **`.head(n)`** / **`.tail(n)`**, **`.cutFields` / `.cutOrdered` / `.cutExcept`**, **`.stats1("count", "qty")`**, **`.splitBy("shape")`**, **`.putQuiet(…)`**. **`.verb(Mlr.Verbs.foo(…))`** 는 위 패턴으로 표현하기 어려울 때만 보조적으로 쓰면 됩니다.

`cut`의 `-o` / `-x` / `-f`는 **`CutFlags`**와 **`.cutOrdered` / `.cutFields`** 등, 또는 **`.cut(option(…), option(…))`**. `head`/`tail` 개수는 **`.head(4)`** / **`.tail(4)`** 또는 **`HeadTail.n(4)`**. `stats1`의 `-a`/`-f`/`-g`는 **`StatsFlags`**. `put -q`는 **`.putQuiet(…)`**. `split -g`는 **`.splitBy("shape")`**. 여러 동사에 공통인 **`MillerVerbOpts.groupBy("field")`** (`head -g` 등)도 씁니다.

`SortFlags`의 **`n("field")` / `nr("field")` / `f("field")`** 와 **`n()`** (값 없는 `-n`, `head`용)은 그대로 씁니다.

```java
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;

String runResult = Mlr.csv()
	.workDir(workingPath)
	.sort(n("a"), nr("b"))
	.file(new File("example.csv"))
	.run();

// 동일 튜토리얼 흐름
String top3 = Mlr.inDir(workingPath)
	.icsv()
	.opprint()
	.sort(nr("index"))
	.head(3)
	.file("example.csv")
	.run();
```

Miller `filter` / `split` 동사는 체인에서 **`.filterVerb(…)`**, **`.splitVerb(…)`** 로 부릅니다.

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

1. ~~execute mlr then connect output stream to isr~~ — `Mlr#run(InputStreamReader)` 및 `redirectOutputFile`로 stdin/stdout 처리 가능
