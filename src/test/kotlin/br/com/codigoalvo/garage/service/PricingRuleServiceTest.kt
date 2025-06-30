package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.AbstractPostgresTest
import br.com.codigoalvo.garage.exception.InvalidRequestException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
class PricingRuleServiceTest: AbstractPostgresTest() {

    @Autowired
    lateinit var pricingRuleService: PricingRuleService

    @Test
    fun `deve retornar multiplicador 0_9 para ocupacao menor que 25`() {
        val testCases = listOf(0.0, 0.1, 0.2499)

        testCases.forEach { rate ->
            assertEquals(
                BigDecimal("0.9"),
                pricingRuleService.calculateOccupancyMultiplier(rate),
                "Falha para $rate (${rate*100}%)"
            )
        }
    }

    @Test
    fun `deve retornar multiplicador 1_0 para ocupacao entre 25 e 50`() {
        val testCases = listOf(0.25, 0.3, 0.4999)

        testCases.forEach { rate ->
            assertEquals(
                BigDecimal("1.0"),
                pricingRuleService.calculateOccupancyMultiplier(rate),
                "Falha para $rate (${rate*100}%)"
            )
        }
    }

    @Test
    fun `deve retornar multiplicador 1_1 para ocupacao entre 50 e 75`() {
        val testCases = listOf(0.5, 0.6, 0.7499)

        testCases.forEach { rate ->
            assertEquals(
                BigDecimal("1.1"),
                pricingRuleService.calculateOccupancyMultiplier(rate),
                "Falha para $rate (${rate*100}%)"
            )
        }
    }

    @Test
    fun `deve retornar multiplicador 1_25 para ocupacao maior ou igual a 75`() {
        val testCases = listOf(0.75, 0.8, 0.9999)

        testCases.forEach { rate ->
            assertEquals(
                BigDecimal("1.25"),
                pricingRuleService.calculateOccupancyMultiplier(rate),
                "Falha para $rate (${rate*100}%)"
            )
        }
    }

    @Test
    fun `deve lancar excecao para valores menores que 0`() {
        val invalidRates = listOf(-0.1, -1.0, Double.NEGATIVE_INFINITY)

        invalidRates.forEach { rate ->
            val exception = assertThrows<IllegalArgumentException> {
                pricingRuleService.calculateOccupancyMultiplier(rate)
            }
            assertTrue(exception.message!!.contains("Taxa de ocupação deve ser"))
        }
    }

    @Test
    fun `deve lancar excecao para valores maiores ou iguais a 1`() {
        val invalidRates = listOf(1.0, 1.1, Double.POSITIVE_INFINITY)

        invalidRates.forEach { rate ->
            val exception = assertThrows<IllegalArgumentException> {
                pricingRuleService.calculateOccupancyMultiplier(rate)
            }
            assertTrue(exception.message!!.contains("Taxa de ocupação deve ser"))
        }
    }

    @Test
    fun `deve lancar excecao para NaN`() {
        assertThrows<IllegalArgumentException> {
            pricingRuleService.calculateOccupancyMultiplier(Double.NaN)
        }
    }

}