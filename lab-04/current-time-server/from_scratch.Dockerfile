# This image is built from scratch meaning here the source code is compiled to JAR by Gradle

# Application builder image
FROM eclipse-temurin:17-jdk AS APP_BUILDER

# Set working directory
WORKDIR /current-time-server

# Copy project files excepting those in .dockerignore
COPY . .

# Build application
RUN ./gradlew bootJar --no-daemon


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

COPY --from=APP_BUILDER current-time-server/build/libs/current-time-server-1.0.0.jar .

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/current-time-server/current-time-server-1.0.0.jar" ]
