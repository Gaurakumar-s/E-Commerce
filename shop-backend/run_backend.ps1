$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
.\mvnw.cmd clean spring-boot:run "-Dspring-boot.run.profiles=dev" -DskipTests
