# BookCrossing Server
**BookCrossing** is the implementation of a simple service for the exchange of paper books.

[![Java CI](https://github.com/EnKarin/BookCrossingServer/actions/workflows/build.yml/badge.svg)](https://github.com/EnKarin/BookCrossingServer/actions/workflows/build.yml)
[![GitHub](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/EnKarin/BookCrossingServer/blob/master/LICENSE "MIT")
[![codecov](https://codecov.io/gh/EnKarin/BookCrossingServer/branch/master/graph/badge.svg)](https://codecov.io/gh/EnKarin/BookCrossingServer)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.enkarin%3Abookcrossing&metric=coverage)](https://sonarcloud.io/summary/new_code?id=io.github.enkarin%3Abookcrossing)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=io.github.enkarin%3Abookcrossing&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=io.github.enkarin%3Abookcrossing)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=io.github.enkarin%3Abookcrossing&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=io.github.enkarin%3Abookcrossing)
[![Mutation testing badge](https://img.shields.io/endpoint?style=plastic&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2FEnKarin%2FBookCrossingServer%2Fmaster)](https://dashboard.stryker-mutator.io/reports/github.com/EnKarin/BookCrossingServer/master)

### Requirements
1. Java 21 and above
2. Maven
3. PostgreSQL

### The service provides the following functions:
- User registration/authorization
- List of users
- Updating profile data
- Adding/removing a book description
- List of books
- Basic messenger
- Bookmarks
- Administrative functions for blocking/unblocking users

### Building
```shell
git clone https://github.com/EnKarin/BookCrossingServer.git
cd BookCrossingServer
./mvnw clean package
```

#### Build Docker image
##### With arm64 support
```shell
./mvnw clean package docker:build -DskipTests
```

##### Without arm64 support
```shell
./mvnw spring-boot:build-image -DskipTests
```

### Running locally
#### Run PostgreSQL database only
```shell
docker-compose --project-name="pg-bookscrossing" up -d
```

##### Stop
```shell
docker-compose --project-name="pg-bookscrossing" down
```

#### Run application in Docker
```shell
docker-compose --file docker-compose-full.yml --project-name="bookscrossing-full" up -d
```

##### Stop
```shell
docker-compose --project-name="bookscrossing-full" down
```

### Documentation
[Swagger UI interactive documentation for localhost](https://localhost:8443/swagger-ui.html)

## Issue Tracking
Found a bug? Have an idea for an improvement? Feel free to [file an issue](../../issues).
