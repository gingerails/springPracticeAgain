FROM gradle:7.6-jdk11 as builder
WORKDIR /app
COPY . .
RUN ./gradlew build --stacktrace

FROM openjdk
WORKDIR /app
EXPOSE 80
COPY --from=builder /app/build/libs/springPracticeAgain-0.0.1.jar .
CMD java -jar springPracticeAgain-0.0.1.jar