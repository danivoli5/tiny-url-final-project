# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built application JAR file into the container
COPY target/tinyurl-0.0.1-SNAPSHOT.jar app.jar

# Copy application.properties to /app/config/ inside the container
COPY src/main/resources/application.properties /app/config/application.properties

# Expose the port
EXPOSE 8080

# Run the application and explicitly set the properties file
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/config/application.properties"]
