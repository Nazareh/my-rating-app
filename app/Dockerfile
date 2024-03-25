FROM bellsoft/liberica-runtime-container:jdk-21-stream-musl as build
COPY . .
RUN ./gradlew clean build
RUN ls

FROM bellsoft/liberica-runtime-container:jre-21-stream-musl
ARG GOOGLE_APPLICATION_CREDENTIALS_BASE_64=init
ENV GOOGLE_APPLICATION_CREDENTIALS_BASE_64=GOOGLE_APPLICATION_CREDENTIALS_BASE_64

COPY --from=build build/libs/my-rating-app-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]