[![Build Status](https://travis-ci.org/valery1707/test-websocket.svg?branch=master)](https://travis-ci.org/valery1707/test-websocket)

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
