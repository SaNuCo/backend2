package de.isweariwillhaveabackend.graphql

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.server.spring.execution.DefaultSpringGraphQLContextFactory
import de.isweariwillhaveabackend.model.User
import de.isweariwillhaveabackend.repositories.UserRepository
import graphql.scalars.datetime.DateTimeScalar
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLType
import io.github.graphglue.connection.filter.TypeFilterDefinitionEntry
import kotlinx.coroutines.reactor.awaitSingle
import org.neo4j.driver.Driver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager
import org.springframework.web.reactive.function.server.ServerRequest
import java.time.OffsetDateTime
import kotlin.reflect.KType
import kotlin.reflect.full.createType

@Configuration
class GraphQLConfiguration {

    @Bean
    fun schemaGeneratorHooks() = object : SchemaGeneratorHooks {
        override fun willGenerateGraphQLType(type: KType): GraphQLType? {
            return when (type.classifier) {
                OffsetDateTime::class -> DateTimeScalar.INSTANCE
                else -> null
            }
        }
    }

    /**
     * Filter factory for [OffsetDateTime] properties
     *
     * @return the generated filter factory
     */
    @Bean
    fun dateTimeFilter() =
        TypeFilterDefinitionEntry(OffsetDateTime::class.createType(nullable = true)) { name, property, parentNodeDefinition, _ ->
            DateTimeFilterDefinition(
                name, parentNodeDefinition.getNeo4jNameOfProperty(property), property.returnType.isMarkedNullable
            )
        }

    @Bean
    fun reactiveTransactionManager(
        driver: Driver, databaseNameProvider: ReactiveDatabaseSelectionProvider
    ): ReactiveNeo4jTransactionManager {
        return ReactiveNeo4jTransactionManager(driver, databaseNameProvider)
    }

    @Bean
    fun contextFactory(userRepository: UserRepository) = object : DefaultSpringGraphQLContextFactory() {
        override suspend fun generateContextMap(request: ServerRequest): Map<*, Any> {
            val username = request.headers().firstHeader("Authorization")!!
            var user = userRepository.findUserByUsername(username)
            if (user == null) {
                user = userRepository.save(User(username, username, false, null)).awaitSingle()
            }
            return super.generateContextMap(request) + mapOf(User::class to user!!)
        }
    }

}

val DataFetchingEnvironment.user get() = this.graphQlContext.get<User>(User::class)