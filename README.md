# What is this?
Trial POC for google analytics API

# how to play
- Download this repo
- FIll in the statics in the GoogleAnalyticsReportingApiPoc
- Amend the reports static methods (e.g. `private static GetReportsResponse ...`)  to get the report / metrics / dimensions you have.
- Run `mvn clean compile assembly:single` in the folder (or, in IntelliJ, the build configuraiotn).
- Run `java -cp target/google-analytics-api-prova-1.0-SNAPSHOT-jar-with-dependencies.jar com.amperity.app.GoogleAnalyticsReportingApiPoc` (or run in Intellij)

#Demo and resources
https://developers.google.com/analytics/devguides/reporting/core/v4/quickstart/service-java

Note that this demo has some issues with dependencies, this project should have them set out. 

Metrics live here https://ga-dev-tools.appspot.com/dimensions-metrics-explorer/



