# Miller Java Binder

Java에서 [Miller (`mlr`)](https://miller.readthedocs.io/)를 직접 호출할 때 생기는 불편을 줄이기 위한 라이브러리입니다.

## 목적

- **호출 방식**: Miller의 동작을 가능한 한 **Java 메서드·객체 조합**으로 표현합니다. 문자열로 긴 명령을 이어 붙이는 대신 `Flag`, `Verb`, `Option` 등으로 조립합니다.
- **컴파일 타임 검증**: API로 조립한 부분(플래그 이름, 동사 이름, 인자 순서 등)은 타입과 메서드 시그니처 수준에서 잡을 수 있습니다. 다만 Miller DSL(예: `put`의 표현식)이나 필드 이름의 유효성은 **런타임에 `mlr`이 검사**하므로, 그 부분까지 컴파일로 막을 수는 없습니다.
- **Miller 문법 몰라도 사용**: 자주 쓰는 입출력 형식은 `Flags`의 정적 팩토리로, 동사는 `Verb` / `Verbs`의 정적 팩토리로 노출되어 있어, Miller CLI 문서를 모두 외우지 않아도 Java 관용에 가깝게 쓸 수 있습니다. 세부 옵션은 여전히 `Flag`, `Objective`, `Option`으로 Miller와 동일한 인자를 넘깁니다.
- **실행**: 내부적으로는 구성한 인자 목록으로 `ProcessBuilder`가 `mlr` 프로세스를 띄웁니다. 시스템에 `mlr`이 설치되어 있고 `PATH`(또는 지정한 경로)에서 실행 가능해야 합니다.

## 범위와 한계

- **거의 모든 동사**: `Verbs` / `Verb`에 Miller 동사 이름에 대응하는 팩토리가 많이 포함되어 있습니다. **전역 플래그**는 upstream [reference-main-flag-list](https://github.com/johnkerl/miller/blob/main/docs/src/reference-main-flag-list.md)를 따라 `Flags`에 정적 팩토리로 두었으며, 갱신 시 `python3 utils/gen_flags.py`로 재생성합니다. 문서에 없는 플래그는 `Flags.raw("--name")` / `Flags.raw("--name", "value")` 또는 `new Flag("...")`로 넘깁니다. `--mfrom` / `--mload`처럼 인자 뒤에 `--`가 오는 형태는 `MlrBinder#mfrom` / `#mload`를 사용합니다.
- **네이티브 바인딩 아님**: Miller를 JVM 안에 링크한 것이 아니라 **외부 `mlr` 실행**입니다.
- **오류 시점**: 잘못된 조합의 일부는 `run()` / `run(InputStreamReader)` 시점에 exit code와 stderr로 드러납니다.

## Testing

1. Jacoco code coverage
	* ref : https://docs.gradle.org/7.4.1/userguide/jacoco_plugin.html
	* testing

		```
		./gradlew test
		google-chrome ./lib/build/jacocoHtml/index.html
		google-chrome ./lib/build/reports/tests/test/index.html
		```

## Examples

```java
import static net.shed.mlrbinder.Flags.csv;
import static net.shed.mlrbinder.verb.Verbs.sort;

import net.shed.mlrbinder.Flag;
import net.shed.mlrbinder.MlrBinder;
import net.shed.mlrbinder.verb.Option;

// 저수준 조립(플래그·동사·옵션을 명시)
String runResult = new MlrBinder("mlr", workingPath)
	.workingPath(workingPath)
	.flag(csv())
	.verb(
		sort()
			.addArg(new Option(new Flag("-n").objective("a")))
			.addArg(new Option(new Flag("-nr").objective("b")))
	)
	.file("example.csv")
	.run();

// 동사에 Flag 인자를 바로 넘기는 형태(정적 팩토리 + 가변 인자)
String runResult2 = new MlrBinder("mlr", workingPath)
	.workingPath(workingPath)
	.flag(csv())
	.verb(
		sort(
			new Flag("-n").objective("a"),
			new Flag("-nr").objective("b")
		)
	)
	.file("example.csv")
	.run();
```

### Fluent entry (`MlrBinder.csv()`, `sort`, `file(File)`)

`SortFlags` provides Miller `sort` key shorthands `n` / `nr` (static import recommended).

```java
import static net.shed.mlrbinder.SortFlags.n;
import static net.shed.mlrbinder.SortFlags.nr;

String runResult = MlrBinder.csv()
	.workDir(workingPath)
	.sort(n("a"), nr("b"))
	.file(new File("example.csv"))
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
