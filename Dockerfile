FROM maven:3.8.6-eclipse-temurin-19-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:19-jdk
#ADD target/backend-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar
COPY --from=build /home/app/target/backend-1.0.0.jar /usr/local/lib/app.jar

ARG NEO4J_URI_COMPOSE
ARG NEO4J_PASSWORD_COMPOSE
ARG REDIS_HOST_COMPOSE
ARG REDIS_PORT_COMPOSE

ENV NEO4J_URI ${NEO4J_URI_COMPOSE}
ENV NEO4J_PASSWORD ${NEO4J_PASSWORD_COMPOSE}
ENV REDIS_HOST ${REDIS_HOST_COMPOSE}
ENV REDIS_PORT ${REDIS_PORT_COMPOSE}

EXPOSE 6062

ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
