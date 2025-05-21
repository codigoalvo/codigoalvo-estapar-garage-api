package br.com.codigoalvo.garage.dto

import br.com.codigoalvo.garage.annotation.NoArg
import java.time.LocalDate

@NoArg
data class RevenueRequest(
    val date: LocalDate,
    val sector: String
)
