FROM bellsoft/liberica-runtime-container:jdk-21-stream-musl as build
WORKDIR /usr/src/app
COPY . .
ARG GOOGLE_APPLICATION_CREDENTIALS_BASE64
RUN ./gradlew clean build

FROM bellsoft/liberica-runtime-container:jre-21-stream-musl
WORKDIR /usr/src/app
ARG GOOGLE_APPLICATION_CREDENTIALS_BASE64
RUN echo $GOOGLE_APPLICATION_CREDENTIALS_BASE64 | base64 -d > credentials.json
ENV GOOGLE_APPLICATION_CREDENTIALS=credentials.json
COPY --from=build /usr/src/app/build/libs/my-rating-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar ${0} ${@}"]