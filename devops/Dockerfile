FROM adoptopenjdk/openjdk11:ubi-minimal

COPY build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]