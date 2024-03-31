FROM maven:latest as mvn

CMD ["mvn", "clean", "install"]

FROM openjdk:21 AS java

COPY target/security-jwt-auth-1.0-SNAPSHOT.jar /opt/

EXPOSE 8080

CMD ["java", "-jar", "/opt/security-jwt-auth-1.0-SNAPSHOT.jar"]
#ENTRYPOINT exec java -jar /opt/security-jwt-auth-1.0-SNAPSHOT.jar