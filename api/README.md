# Canteen GraphQL API

## General
- This is the SoPra WS20/21 api project for a GraphQL API with Spring Boot and kotlin.
- It exposes three endpoints
    - `http://localhost:8080/api/public/`
        - Handles all public functionality
            - Read (basically everything except comments, ratings and votes)
            - Create comments, rate DayMeal, vote on MealProposal
        - `api/public` is the main GraphQL endpoint
        - `api/public/images` used to get image
    - `http://localhost:8080/api/internal/`
        - Handles all internal functionality
            - Create, update and delete domain entities (Meals, Categories, Labels, Ingredients, DayMeals, ...)
            - All functionality of the public endpoint also works on the internal endpoint
         - `api/internal` is the main GraphQL endpoint
         - `api/internal/images` used to get image and put images
    - `http://localhost:8080/api/token/`
        - Single endpoint, used to get tokens for rating
        - The token is necessary to perform any write mutations (create comment, rate DayMeal, vote on MealProposal) which are available on the public endpoint. This can be used for example to allow all users to see the menu (even if they are not in the internal network), while only allowing users which were at least once in the local network to vote / rate / create comments to prevent spam
- It also exposes a GraphQL playground which can be used for development and documentation under `http://localhost:8080/api/playground` if the development docker-compose file or `./gradlew bootRun` is used

## Usage
It is highly recommended to use the api with docker-compose

### With docker-compose
See [README in project home](../README.md#With-docker-compose)

### Without docker-compose

#### Prerequisites
- Install a JDK in at least version 11.
- Make sure that `JAVA_HOME` is set correctly to the root directory of your JDK. You can check with this command: `echo %JAVA_HOME%` (or `echo $JAVA_HOME` on Linux / Git Bash)
- Make sure that the JDK `bin` folder is added to your `PATH`.
- You can make sure that java is correctly installed with `java -version`
- You can also install gradle yourself, but if you use the gradle Wrapper scripts (`gradlew` or `gradlew.cmd`), this is not necessary, since gradle will be downloaded in this case.
- Set the necessary database environment variables:
    - `DB_URL`: The jdbc connection url string to a MariaDB database
    - `DB_USERNAME`: The user with access to the database. Needs access to the canteen database
    - `DB_PASSWORD`: The password for the user
    - `GRAPHQL_PLAYGROUND_ENABLED`: if `true`, the GraphQL playground is available, if `false` it is not available
    - The environment variables are not necessary for test only (an in memory database is used)

#### Execution
```bash
# execute tests and generate test coverage report
./gradlew test

# build and run
./gradlew bootRun
```
