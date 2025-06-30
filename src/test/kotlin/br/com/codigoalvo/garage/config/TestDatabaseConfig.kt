package br.com.codigoalvo.garage.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@TestConfiguration
class TestDatabaseConfig {

    @Bean
    @Primary
    fun testDataSource(): DataSource {
        return DataSourceBuilder.create()
            .url("jdbc:tc:postgresql:13-alpine:///testdb?TC_TMPFS=/testtmpfs:rw")
            .username("test")
            .password("test")
            .driverClassName("org.testcontainers.jdbc.ContainerDatabaseDriver")
            .type(HikariDataSource::class.java)
            .build()
            .apply {
                maximumPoolSize = 3
                maxLifetime = 30000
                leakDetectionThreshold = 5000
            }
    }
}