Neon release tracker
-------------
A simple release tracker built with Spring boot sitting on top of H2 DB.

### Implementation

The system is build using Spring Boot and Java 1.8. The following modules are included to build the app:
* Web
* Lombok
* H2

Possible actions:
* GET list and filter releases
* GET a single release
* POST create new release
* PUT update a release
* DELETE a release

To see the full list of possible actions you can check `swagger`.

Release is modeled in the following way:
| Release |
|------|
| id PK |
| title IX |
| description |
| status |
| release_date |
| created_date |
| last_updated_at |

Valid statuses
* Created
* In Development
* On DEV
* QA Done on DEV
* On staging
* QA done on STAGING
* On PROD
* Done

### Building, Running and Testing

```bash
$ ./mvnw clean spring-boot:run
```
or alternatively using your installed maven version

```bash
mvn clean spring-boot:run
```

### Dockerizing app

```bash
$ mvn clean package
$ docker build -t neon-release-tracker:latest .
```

running docker image

```bash
$ docker run -it -p 8080:8080 neon-release-tracker
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.