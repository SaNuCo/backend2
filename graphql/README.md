# graph-glue demo

Demo application for graph-glue

Steps to run:

1. Install graph-glue in its current version:
    ```sh
    git clone https://github.com/graphglue/graph-glue.git
    cd graph-glue
    ./gradlew publishToMavenLocal
    ```

2. Start the db
    ```sh
    docker compose up
    ```

3. Run the example
    ```sh
    ./gradlew bootRun