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

## TODO

### v0.01

1. ~~logging~~
	1. ~~info to debug~~
1. ~~change to gradle library project~~
1. ~~add E2E test~~

### v0.02

1. add static prebuilt object
1. execute mlr then connect output stream to isr

		public void run(InputStreamReader isr) {

		}
1. remove isConsecutive from the Verb