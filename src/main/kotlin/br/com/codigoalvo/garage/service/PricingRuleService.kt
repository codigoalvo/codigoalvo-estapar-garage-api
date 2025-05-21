package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.config.PricingProperties
import br.com.codigoalvo.garage.domain.model.Sector
import br.com.codigoalvo.garage.domain.model.Spot
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ceil

@Component
class PricingRuleService(
    private val pricingProperties: PricingProperties
) {

    private val logger = LoggerFactory.getLogger(PricingRuleService::class.java)

    fun calculateTimePeriodInMinutes(entryTime: LocalDateTime?, exitTime: LocalDateTime?): Long {
        return Duration
            .between(entryTime, exitTime)
            .toMinutes()
            .coerceAtLeast(1).also {
                logger.info("Tempo de duração da estadia calculada em minutos entre [$entryTime] e [$exitTime] : $it")
            }
    }

    fun calculateOccupancyRate(sector: Sector): Double {
        val capacity = sector.capacity
        val occupiedSpots = sector.spots.count { it.isOccupied }
        val occupancyRate = occupiedSpots.toDouble() / capacity
        val totalSpots = sector.spots.size

        logger.info("Ocupação do setor [${sector.code}]")
        logger.info("-> Capacidade de vagas: $capacity")
        logger.info("-> Total de vagas: $totalSpots")
        logger.info("-> Vagas ocupadas: $occupiedSpots")
        logger.info("Taxa de ocupação no momento do PARKED: $occupancyRate")
        return occupancyRate
    }

    fun calculateOccupancyMultiplier(occupancyRate: Double): BigDecimal {
        require(occupancyRate >= 0 && occupancyRate < 1.0) {
            "Taxa de ocupação deve ser >= 0% e < 100% (valor recebido: ${occupancyRate * 100}%)"
        }
        return pricingProperties.rules
            .sortedByDescending { it.threshold }
            .firstOrNull { occupancyRate >= it.threshold }
            ?.multiplier
            ?.toBigDecimal()
            ?: pricingProperties.rules.first().multiplier.toBigDecimal()
                .also {
                    logger.info("Multiplicador de ocupação ($occupancyRate taxa) calculado: $it")
                }
    }

    fun calculateDurationMultiplier(durationMinutes: Long): BigDecimal {
        return if (durationMinutes <= 60) {
            BigDecimal.ONE
        } else {
            val extraMinutes = durationMinutes - 60
            val extraQuarters = ceil(extraMinutes / 15.0).toInt()
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