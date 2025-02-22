# This image is built from already compiled executables by Gradle
# It works faster for subsequent runs but needs for `./gradlew bootJar` to be executed

# Application executable image
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /current-time-server

# Add user 'user' and give them permissions for `/current-time-server` dir
RUN addgroup --system user && \
    adduser --system --group user && \
    chown user /current-time-server
# Switch to user 'user'
USER user

COPY build/libs/current-time-server-1.0.0.jar .

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/current-time-server/current-time-server-1.0.0.jar" ]
