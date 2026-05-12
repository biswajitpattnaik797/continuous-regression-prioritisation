# Continuous Regression Prioritisation

This project demonstrates an automated API regression testing environment integrated with continuous integration.

## Tools Used

- Java 8
- Maven
- TestNG
- Rest Assured
- IntelliJ IDEA
- Jenkins
- GitHub

## Test Environment

The tests use the public JSONPlaceholder API:

https://jsonplaceholder.typicode.com

## Testing Techniques Used

- Automated API regression testing
- Test case prioritisation using TestNG priorities
- Test grouping using smoke, critical and regression groups
- Continuous integration using Jenkins

## Test Strategy

Smoke and critical tests are executed first to provide fast feedback. Full regression tests are executed later to validate wider functionality.

## Test Suite

Total automated tests: 8

## Local Execution

Run tests from IntelliJ by right-clicking `ReqresRegressionTests.java` and selecting Run.

## Expected Result

All 8 tests should pass.