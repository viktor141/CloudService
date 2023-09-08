FROM openjdk:17-oracle
EXPOSE 8081
ADD build/libs/CloudService-0.0.1-SNAPSHOT.jar cloudService.jar
CMD ["java", "-jar", "cloudService.jar"]