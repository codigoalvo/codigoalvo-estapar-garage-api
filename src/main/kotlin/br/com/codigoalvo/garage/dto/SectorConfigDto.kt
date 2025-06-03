package br.com.codigoalvo.garage.dto

import com.fasterxml.jackson.annotation.JsonAlias
import java.math.BigDecimal

data class SectorConfigDto(

    @JsonAlias("sector")
    val sector: String = "",

    @JsonAlias("basePrice")
    val basePrice: BigDecimal = BigDecimal.ZERO,

    @JsonAlias("maxCapacity")
    val maxCapacity: Int = 0,

    @JsonAlias("openHour")
    val openHour: String = "",

    @JsonAlias("closeHour")
    val closeHour: String = "",

    @JsonAlias("durationLimitMinutes")
    val durationLimitMinutes: Int = 0
)