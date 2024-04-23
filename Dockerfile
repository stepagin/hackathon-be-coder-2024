FROM eclipse-temurin
ARG JAR_FILE=target/*.jar
COPY ./target/be-coder-1.1.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]