# WeatherService
A web service that returns the current wind conditions for a given zip code.

## Constraints
* Framework: Spring Boot
* Dependency Manager: Gradle or Maven
* Test Framework: JUnit and Mockito/Powermock
* Coding Style Guide: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

## Design Goals
* Bind your weather client to an interface, and not to the concrete implementation of the OpenWeatherMap API.
* Implement the cache handler as a decorator for the weather client.
* Bind services to an interface (not an implementation) in the service container.

## Functional Requirements
* Consume weather data from https://openweathermap.org/.
* Provide an HTTP GET /wind/{zipCode} method that takes a zipcode as a required path parameter and returns a wind resource.
* Validates input data.
* Response format should be JSON.
* Cache the resource for 15 minutes to avoid expensive calls to the OpenWeatherMap API.
* Provide a CLI command that will bust the cache if needed.
* Ensure that the cache is thread safe.
* Response fields should include: (a) Wind Speed, (b) Wind Direction

## Unit Testing Requirements
* Use mock responses for the OpenWeatherMap API.
* Use mocks when interacting with the cache layer.

## How To Run
* Clone the repository.
* Build project and generate the JAR.
* Execute the project JAR.
* The wind resource should now be accessible by running a curl command:

`$ curl -x http://localhost:8080/api/v1/wind/89101`
