FROM bellsoft/liberica-runtime-container:jdk-21-stream-musl as build
COPY . .
RUN ./gradlew clean build

FROM bellsoft/liberica-runtime-container:jre-21-stream-musl
COPY --from=build build/libs/my-rating-app-0.0.1-SNAPSHOT.jar app.jar
ENV GOOGLE_APPLICATION_CREDENTIALS_BASE64=$GOOGLE_APPLICATION_CREDENTIALS_BASE64
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]