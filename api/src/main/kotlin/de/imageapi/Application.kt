package de.imageapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

/**
 * Application used to start GraphQL api
 */
@SpringBootApplication
@EntityScan("de.imageapi.model.entities")
class Application

/**
 * Main function, runs the api server application
 * @param args used to run the application
 */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
