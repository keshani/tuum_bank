FROM openjdk:17

# Set the working directory to /tuumbank
WORKDIR /app

# Copy the application JAR file to the container
COPY build/libs/bank-0.0.1-SNAPSHOT.jar /app/bank-0.0.1-SNAPSHOT.jar

# Expose the port that the application runs on
EXPOSE 9093

# Set the entry point for the image to run the application
ENTRYPOINT ["java", "-jar", "/app/bank-0.0.1-SNAPSHOT.jar"]