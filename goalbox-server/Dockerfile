ARG contracts=goalbox-contract
ARG module=goalbox-server


FROM gradle:7.5.1-jdk17 AS build

ARG contracts
ARG module

COPY ${contracts} ${contracts}
COPY ${module} ${module}

WORKDIR ${module}

RUN gradle build --no-daemon


FROM openjdk:17.0.2-jdk-slim

ARG module

COPY --from=build /home/gradle/${module}/build/libs/*.jar /app.jar

EXPOSE 8080

CMD exec java ${JAVA_OPTS} -jar /app.jar