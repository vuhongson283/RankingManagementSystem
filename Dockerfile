FROM openjdk:21

COPY ./target/rms.jar ./rms.jar

#container port
EXPOSE 3000

#docker run -p 8080:3000 my-app    (command for mapping host's port to container's port)
CMD ["java", "-jar","rms.jar"]