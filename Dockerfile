FROM ubuntu AS download
ARG version
RUN echo $version
RUN mkdir /app
ADD https://github.com/yunkuangao/url-random/releases/download/$version/url-random-all.jar /app/

FROM amazoncorretto:17
RUN mkdir /app
EXPOSE 8080:8080
COPY --from=download /app/url-random-all.jar /app/url-random.jar
RUN ls /app
ENTRYPOINT ["java","-jar","/app/url-random.jar"]