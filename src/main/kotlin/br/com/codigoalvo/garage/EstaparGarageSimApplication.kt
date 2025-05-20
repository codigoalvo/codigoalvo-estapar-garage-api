package br.com.codigoalvo.garage

import br.com.codigoalvo.garage.config.PricingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(PricingProperties::class)
@SpringBootApplication
class EstaparGarageSimApplication

fun main(args: Array<String>) {
    runApplication<EstaparGarageSimApplication>(*args)
}
