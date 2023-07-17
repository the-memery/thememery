FROM maven:3.8.1-jdk-11

WORKDIR /app

COPY .mvn/ ./mvn
COPY mvnw pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package -DskipTests

EXPOSE 8090

CMD ["mvn", "spring-boot:run"]

LABEL org.opencontainers.image.source="https://github.com/corentinbaudet/thememery"