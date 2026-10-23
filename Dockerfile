FROM eclipse-temurin:17-jre

WORKDIR /app

RUN groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring

COPY --chown=spring:spring target/java-demo-app.jar app.jar

USER spring:spring

EXPOSE 8080
ENV SERVER_PORT=8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
