FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y git
RUN java --version
RUN git clone https://github.com/joosing/fireball.git
RUN cd fireball
RUN ./mvnw dependency:resolve
RUN ./mvnw clean package -DskipTests

ENTRYPOINT java -Dfireball.server.root-path="/app/files" -Dfireball.client.root-path="/app/files" -jar ./target/fireball-0.0.1.final.jar

# I have encountered the error "/bin/sh: 1: ./mvnw: not found" while building a docker image
# This happens when Windows OS uses "\r\n" as an internal newline in the mvnw file
# Changing the file newline to "\n" fixes the problem
# You can refer to the following GitHub issues.
# https://github.com/jhipster/jhipster-registry/pull/476
# https://github.com/jhipster/jhipster-registry/issues/377