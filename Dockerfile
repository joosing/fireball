FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY ../.mvn .mvn
COPY ../mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY ../src ./src

ENTRYPOINT ./mvnw spring-boot:run

# I have encountered the error "/bin/sh: 1: ./mvnw: not found" while building a docker image
# This happens when Windows OS uses "\r\n" as an internal newline in the mvnw file
# Changing the file newline to "\n" fixes the problem
# You can refer to the following GitHub issues.
# https://github.com/jhipster/jhipster-registry/pull/476
# https://github.com/jhipster/jhipster-registry/issues/377