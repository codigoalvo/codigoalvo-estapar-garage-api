package br.com.codigoalvo.garage.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "pricing")
@Configuration
class PricingProperties {
    var rules: List<PricingRule> = emptyList()

    data class PricingRule(
        var threshold: Double = 1.0,
        var multiplier: Double = 1.0
    )
}
