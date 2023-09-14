FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
ENV TZ=America/New_York
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]