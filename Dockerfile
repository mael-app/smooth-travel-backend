FROM registry.access.redhat.com/ubi9/openjdk-21:1.24 AS build

USER root
WORKDIR /build

RUN microdnf install -y gzip && microdnf clean all

COPY pom.xml mvnw ./
COPY .mvn .mvn
# dependency:go-offline fails on SNAPSHOT transitive deps not managed by the Quarkus BOM.
# Running package instead lets the BOM resolve versions correctly; target is removed
# so this layer only serves as a dependency cache.
RUN ./mvnw -B package -DskipTests -q && rm -rf target

COPY src src
RUN ./mvnw package -DskipTests -q

FROM registry.access.redhat.com/ubi9/openjdk-21:1.24

ENV LANGUAGE='en_US:en'

COPY --from=build --chown=185 /build/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build --chown=185 /build/target/quarkus-app/*.jar /deployments/
COPY --from=build --chown=185 /build/target/quarkus-app/app/ /deployments/app/
COPY --from=build --chown=185 /build/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
