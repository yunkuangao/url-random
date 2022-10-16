FROM gradle:7-jdk17 AS build
WORKDIR /usr/app/
COPY build.gradle.kts settings.gradle.kts gradle.properties /usr/app/

COPY gradle /usr/app/gradle
COPY --chown=gradle:gradle . /home/gradle/src
USER root
RUN chown -R gradle /home/gradle/src
#RUN chown -R gradle /home/gradle/build

RUN gradle buildFatJar || return 0
COPY . .
RUN gradle clean build

FROM openjdk:17
EXPOSE 8080:8080
WORKDIR /usr/app/
COPY --from=build /usr/app/build/libs/url-random-all.jar .

ENTRYPOINT ["java","-jar","url-random.jar"]