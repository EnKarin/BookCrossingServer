# BookCrossing Server
**BookCrossing** is the implementation of a simple service for the exchange of paper books.


[![Java CI](https://github.com/EnKarin/BookCrossingServer/actions/workflows/build.yml/badge.svg)](https://github.com/EnKarin/BookCrossingServer/actions/workflows/build.yml)
[![GitHub](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/EnKarin/BookCrossingServer/blob/master/LICENSE "MIT")
[![codecov](https://codecov.io/gh/EnKarin/BookCrossingServer/branch/master/graph/badge.svg)](https://codecov.io/gh/EnKarin/BookCrossingServer)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.enkarin%3Abookcrossing&metric=coverage)](https://sonarcloud.io/summary/new_code?id=io.github.enkarin%3Abookcrossing)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=io.github.enkarin%3Abookcrossing&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=io.github.enkarin%3Abookcrossing)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=io.github.enkarin%3Abookcrossing&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=io.github.enkarin%3Abookcrossing)

### Requirements
1. Java 17 and above
2. Maven
3. MySQL
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
  ~~~ 
  git clone https://github.com/EnKarin/BookCrossingServer.git
  cd BookCrossingServer
  mvn install 
  ~~~
### Documentation
[Swagger UI interactive documentation for localhost](https://localhost:8443/swagger-ui.html)
## Issue Tracking
Found a bug? Have an idea for an improvement? Feel free to [file an issue](../../issues).
