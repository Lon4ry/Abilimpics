FROM alpine:latest as base

RUN apk add --no-cache openjdk17 curl

ARG DIR

FROM base as build

WORKDIR /app

COPY ${DIR} .

RUN ./gradlew build -x test

FROM base as runtime

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "./app.jar" ]

FROM base as local-runtime

WORKDIR /app

COPY ${DIR}build/libs/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "./app.jar" ]