# Discover-services Spring boot Application

This Spring Boot application has been developed as part of an assignment with the goal of creating a system that comprises REST APIs communicating with AWS services EC2 instance and S3 bucket. The primary function of this application is to provide APIs for discovering available EC2 instances and S3 buckets within the AWS ecosystem. Upon discovery, the application stores this information in a MySQL database, allowing for efficient retrieval and management of the discovered resources.

## Technologies Used:
- 	Java: Programming language
- 	Spring Boot: Framework for creating standalone, production-grade Spring-based applications
- 	Maven: Dependency management tool
- 	MySQL: Relational database for storing AWS service information
- 	AWS SDK: For integrating with AWS services (EC2, S3)

## Prerequisites

- Java Development Kit (JDK) 17
-	IntelliJ IDEA / Eclipse
-	MySQL Workbench and server

## Steps to run the application
1. Clone or download the Spring Boot project from the repository (if not already available).    
   ```bash
   git clone https://github.com/srinivasa8/discover-services.git
2.	Please run the below database create scripts before proceeding to next step
      ```bash
      https://github.com/srinivasa8/SQL_Scripts/blob/main/SCRIPTS_FOR_DISCOVER_SERVICES_APPLICATION_ASSIGNMENT.sql
3.	Open the project using an IDE like IntelliJ IDEA, Eclipse, or Spring Tool Suite.

4.	Locate the application.properties and replace the below environment variables with the actual values on application.properties file:
      
      ```bash
      spring.datasource.url=${SPRING_DATASOURCE_URL}
      spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
      spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
 
      application.aws.accesskey=${APPLICATION_AWS_ACCESS_KEY}
      application.aws.secretkey=${{APPLICATION_AWS_SECRET_KEY}
      ```

5.	Using the terminal or within your IDE, execute mvn clean install to build the project and ensure the build completes successfully without any errors.
6.	Locate the main class (annotated with @SpringBootApplication) and Right-click on the main class and select "Run" to start the application.
7.	Check the console output for application logs and startup messages and application should start and display a message indicating that the server has started.
      If the application started successfully, you will see the below messages
       ```bash
      Tomcat started on port 8080 (http) with context path
      Started DiscoverServicesApplication in 16.804 seconds (process running for 14.485)
      ```
8.	Once the application is running, access defined endpoints by opening a web browser or using tools like Postman or swagger and navigating to http://localhost:8080 followed by the endpoint path.
      ```bash 
      E.g.: API URL : http://localhost:8080/api/getDiscoveryResult?service=S3
9. For more information on the APIs, After running the application, Please access the [swagger documentation here](http://localhost:8080/swagger-ui/index.html).