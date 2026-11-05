# Образ збирається з готового jar: CI виконує `mvn package` сам, бо hexocnc-core лежить у приватному
# GitHub Packages і потребує авторизації. Локально: mvn -DskipTests package && docker build -t leads-service .
FROM eclipse-temurin:25-jre
RUN useradd --system --uid 1001 --no-create-home spring
WORKDIR /app
# logs — тека для logging.file.name=./logs/leadservice.log з application.yaml
RUN mkdir -p /app/logs && chown spring /app/logs
COPY target/leads-service-*.jar app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-jar", "/app/app.jar"]
