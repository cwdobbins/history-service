# history-service
Prototype for search history service, using jetbrains/exposed and postgresql

## Setup
This project depends on a postgresdb.  I will improve the tooling for standing this up in the near future by incorporating a gradle task, but for now, run docker and deploy a postgres docker image:

`docker run --rm -P --publish 0.0.0.0:5432:5432 --name postgres -e POSTGRES_PASSWORD=testy -d postgres`

## Building

`gradlew clean build`

## Running Tests 

`gradlew test`
