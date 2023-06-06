FROM maven:3.8.7-openjdk-18 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:18
ENV DEBIAN_FRONTEND noninteractive
COPY --from=build /usr/src/app/target/jedisSentinelTLSTest-1.0-SNAPSHOT-jar-with-dependencies.jar /usr/app/jedisSentinelTLSTest-1.0-SNAPSHOT-jar-with-dependencies.jar
COPY --from=build /usr/src/app/src/main/resources/runApplication.sh /usr/app/runApplication.sh
COPY --from=build /usr/src/app/src/main/resources/tls/ /usr/app/tls/
EXPOSE 8080
ENTRYPOINT ["/usr/app/runApplication.sh"]
