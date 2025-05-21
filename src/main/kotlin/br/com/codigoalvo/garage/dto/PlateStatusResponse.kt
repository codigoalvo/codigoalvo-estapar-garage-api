package br.com.codigoalvo.garage.dto

import br.com.codigoalvo.garage.annotation.NoArg
import java.math.BigDecimal
import java.time.LocalDateTime

@NoArg
data class PlateStatusResponse(
    val licensePlate: String,
    val priceUntilNow: BigDecimal,
    val entryTime: LocalDateTime?,
    val timeParked: LocalDateTime? = null
)
