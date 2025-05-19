package br.com.codigoalvo.garage.dto

import java.math.BigDecimal

data class SectorConfigDto(
    val sector: String = "",
    val basePrice: BigDecimal = BigDecimal.ZERO,
    val maxCapacity: Int = 0,
    val openHour: String = "",
    val closeHour: String = "",
    val durationLimitMinutes: Int = 0
)