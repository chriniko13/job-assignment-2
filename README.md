#### Just a Job Assignment

##### How to start/stop external dependencies (MongoDB)
* (For start) Execute the following: <code>docker-compose up</code>

* (For stop) Execute the following: <code>docker-compose down</code>


##### How to run unit tests
* Execute: <code>mvn clean test</code>


##### How to run integration tests (we need to start external dependencies - see above)
* Execute: <code>mvn clean verify</code>


##### How to run unit and integration test coverage via JaCoCo
* Execute: <code>mvn clean package</code>
* Go to: target/site/jacoco/index.html



##### How to build docker image
* First option is via command: <code>mvn install</code> with the help of com.spotify:dockerfile-maven-plugin

* Second option is:
    * Execute: <code>docker build -t job-chriniko/job-chriniko-assignment .</code>    


##### How to run service

* First option is:
    * Execute: <code>java -jar target/job-chriniko-assignment-1.0.jar</code>

* Second option is:
    * Execute: <code>docker run job-chriniko/job-chriniko-assignment:latest</code>

* Third option is:
    * Execute: <code>mvn spring-boot:run</code>
    
    
##### How to manual test:
* In order to see functionality hit the following url with GET operation
  * <code>localhost:8080/job-assignment/text?p_start=1&p_end=10&w_count_min=1&w_count_max=20</code>
  
* In order to see history hiw the following url with GET operation
  * <code>localhost:8080/job-assignment/history</code>