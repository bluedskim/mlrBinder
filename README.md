# Miller Java Binder

helps run mlr(Miller) with java.

## Testing

1. Jacoco code coverage
	* ref : https://docs.gradle.org/7.4.1/userguide/jacoco_plugin.html
	* testing

		```
		./gradlew test
		google-chrome ./app/build/jacocoHtml/index.html
		google-chrome ./app/build/reports/tests/test/index.html
		```

## TODO

1. logging
	1. change default level
	2. info to debug
1. change to gradle library project
1. add MlrBinder.setRedirectOutputFile()