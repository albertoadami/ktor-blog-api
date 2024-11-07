# Use an official JDK image as the base
FROM openjdk:21-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and the build scripts
COPY gradlew ./
COPY gradle/ ./gradle

# Copy the rest of the application source code
COPY . .

# Grant execute permission to the Gradle wrapper
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew installDist

# Use a minimal JDK image for the final image
FROM openjdk:21-slim

# Set the working directory for the final image
WORKDIR /app

# Copy the built application from the previous build stage
COPY --from=build /app/build/install/blog-api /app

# Expose the port your application will run on
EXPOSE 8080

# Command to run the application
CMD ["bin/blog-api"]
