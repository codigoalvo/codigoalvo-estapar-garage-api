package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.config.PricingProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

@Component
class PricingRuleService(
    private val pricingProperties: PricingProperties
) {

    private val logger = LoggerFactory.getLogger(PricingRuleService::class.java)

    fun calculateOccupancyMultiplier(occupancyRate: Double): BigDecimal {
        require(occupancyRate >= 0 && occupancyRate < 1.0) {
            "Taxa de ocupação deve ser >= 0% e < 100% (valor recebido: ${occupancyRate * 100}%)"
        }
        return pricingProperties.rules
            .sortedByDescending { it.threshold }
            .firstOrNull { occupancyRate >= it.threshold }
            ?.multiplier
            ?.toBigDecimal()
            ?: pricingProperties.rules.first().multiplier.toBigDecimal() // Fallback para <25%
                .also {
                    logger.info("Multiplicador de ocupação ($occupancyRate taxa) calculado: $it")
                }
    }

    fun calculateDurationMultiplier(durationMinutes: Long): BigDecimal {
        return if (durationMinutes <= 60) {
            BigDecimal.ONE
        } else {
            val extraMinutes = durationMinutes - 60
            val extraQuarters = Math.ceil(extraMinutes / 15.0).toInt()
            BigDecimal.ONE + (BigDecimal(extraQuarters) * BigDecimal("0.25"))
        }.also {
            logger.info("Multiplicador de periodo de tempo ($durationMinutes minutos) calculado: $it")
        }
    }

    fun calculateCharge(
        basePrice: BigDecimal,
        durationMultiplier: BigDecimal,
        occupancyMultiplier: BigDecimal
    ): BigDecimal {
        val finalAmount = basePrice
            .multiply(occupancyMultiplier)
            .multiply(durationMultiplier)
            .setScale(2, RoundingMode.HALF_UP)
        logger.info("Calculando cobrança:")
        logger.info("-> Base price: $basePrice")
        logger.info("-> Multiplicador de ocupação aplicado: $occupancyMultiplier")
        logger.info("-> Multiplicador de período tempo aplicado: $durationMultiplier")
        logger.info("-> Valor final calculado: $finalAmount")
        return finalAmount
    }

}