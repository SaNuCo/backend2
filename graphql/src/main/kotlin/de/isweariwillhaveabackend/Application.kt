package de.isweariwillhaveabackend

import io.github.graphglue.data.repositories.EnableGraphglueRepositories
import org.neo4j.driver.Driver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication
@EnableGraphglueRepositories
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}