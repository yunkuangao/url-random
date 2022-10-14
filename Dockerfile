FROM openjdk:17
EXPOSE 80:8080
RUN mkdir /app
COPY ./build/libs/url-random-all.jar /app/url-random.jar
ENTRYPOINT ["java","-jar","/app/url-random.jar"]