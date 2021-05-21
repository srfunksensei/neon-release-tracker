FROM amazoncorretto:8
LABEL maintainer="sr.funk.sensei@gmail.com"
COPY target/neon-release-tracker-0.0.1-SNAPSHOT.jar neon-release-tracker.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/neon-release-tracker.jar"]