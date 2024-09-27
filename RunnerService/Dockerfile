# Use a base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar into the container
COPY build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]