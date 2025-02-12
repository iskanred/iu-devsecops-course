# Current Time Server

## Overview
The service provides information about current time in Europe/Moscow timezone and also shows
the number of getting this information (number of visits).

### Language/Framework selection
I decided to use Kotlin language with Spring framework because it is a popular and modern stack and I have proper experience.

## Build the application
### Building the application on host machine
To build the application locally execute the following command:
```shell
./gradlew bootJar
```
Now you can see compiled executables in the `build/lib/` directory.

### Building a Docker image of the application
1. **From the scratch**.

   To build a Docker image of the application execute the following command:
   ```shell
   docker build -f from_scratch.Dockerfile --tag iskanred/current-time-server:{{APP_VERSION}} .
   ```
2. **From compiled executables**

   If you have executed the section `Building the application on host machine` you can
   build Docker image from the compiled executables using another Dockerfile

   ```shell
   docker build -f Dockerfile --tag iskanred/current-time-server:{{APP_VERSION}} .
   ```

## Unit tests
### How to run?
To run tests execute the following command:
```shell
./gradlew test
```

### What is tested?
There are only two logical components in the service: controller, service.
Therefore, there are only two main tests for these components:
1. **Controller**: test for page availability and page's text correctness. **Spring WebMvcTest** is used for test web client.
2. **Service**: test that time will increase after page refresh (new request is received).
### Tools
* [SpringBoot Starter Test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test)
* [Kotlin Test](https://kotlinlang.org/api/latest/kotlin.test/)
* [Mockk](https://mockk.io/)
* [SpringMockk](https://github.com/Ninja-Squad/springmockk)

## Linter
### How to run?
To lint the source code of the application execute the following command:
```shell
./gradlew detekt
```

### What is linted?
I have used code linter named [`detekt`](https://detekt.dev/) for Kotlin.
For linting I have used default `detekt` rules with some overriding.
The changes could be found here: [`config/detekt.yml`](config/detekt.yml).


## Run the application
### GitHub
1. Clone this repo.
2. Run the following command:
    ```shell
    ./gradlew bootRun
    ```
3. Now the application is ready to use
### Docker
1. Pull the image from Docker Hub:
    ```shell
    docker pull iskanred/current-time-server:1.0.0
    ```
2. Run a container based on this image:
    ```shell
    docker run -p 8080:8080 -v "./visits_dir/:/current-time-server/visits_dir" --name current-time-server iskanred/current-time-server:1.0.0
    ```
3. Now the application is ready to use


## Usage
The server's URL is `http://127.0.0.1:8080/`.
1. **Time**: Obtain the page from the browser or make a GET request from any HTTP client.
   The resulting response is a text string with current Moscow time.
2. **Visits**: Making HTTP GET request for URL `http://127.0.0.1:8080/visits` the one can obtain the
   number of times users visited the main page (`http://127.0.0.1:8080/`).
   The information is persistent because it is stored in the file (`visits_dir/visits`)
   with number of visits which is created at the first run of the application.
