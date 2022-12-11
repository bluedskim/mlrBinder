# Miller Java Binder

helps run mlr(Miller) with java.

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

	String runResult = new MlrBinder("mlr", workingPath)
		.workingPath(workingPath)
		.flag(csv())
		.verb(
			sort()
				.addArg(new Flag("-n").objective("a"))
				.addArg(new Flag("-nr").objective("b"))
		)
		.file("example.csv")
		.run()
	;

	String runResult = MlrBinder
		.csv()
		.sort(n("a"), nr("b"))
		.file(new File("example.csv"))
		.run()
	;

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

1. execute mlr then connect output stream to isr

		public void run(InputStreamReader isr) {

		}