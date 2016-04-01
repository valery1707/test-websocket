[![Build Status](https://travis-ci.org/valery1707/test-websocket.svg?branch=master)](https://travis-ci.org/valery1707/test-websocket)
[![Coverage Status](https://coveralls.io/repos/github/valery1707/test-websocket/badge.svg?branch=master)](https://coveralls.io/github/valery1707/test-websocket?branch=master)

Simple project for work with Websocket: `spring-boot`, `jetty-embedded`

### Explanation of technology choice:
1. Build system
	* Alternatives: Maven, Gradle, ANT, SBT
	* Selected: Gradle
	* Reason: Gradle is declarative build system for Java project (same as Maven), that designed to build not only Java projects, and have many possibilities for change behavior of build process
1. Embedded servlet container
	* Alternatives: Tomcat embedded, Jetty
	* Selected: Jetty
	* Reason: Jetty has less footprint (~ 1.4 MiB, ~ 1.7 MiB with websocket implementation) than Tomcat (~ 2.7 MiB, 2.9 MiB with websocket implementation)

### Useful gradle commands
1. Run project from console: `./gradlew bootRun`
1. Build executable jar: `./gradlew build`
1. Build test coverage report: `./gradlew check jacocoTestReport` and open `build/reports/jacoco/test/html/index.html`
1. Run project in WildFly 10.0.0: `./gradlew build cargoRunLocal`
	* Sometimes WildFly could not access to temp file on VFS and fail deploy, in this case you need to stop WildFly and run command second time
	* Sometimes `cargo` could not stop launched application server, in this case you can try stop server by command `./gradlew cargoStopLocal` or kill `java` manual
