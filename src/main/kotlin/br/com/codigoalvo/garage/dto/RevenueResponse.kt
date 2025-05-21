package br.com.codigoalvo.garage.dto

import br.com.codigoalvo.garage.annotation.NoArg
import java.math.BigDecimal
import java.time.LocalDateTime

@NoArg
data class RevenueResponse(
    val amount: BigDecimal,
    val currency: String = "BRL",
    val timestamp: LocalDateTime
)
