package br.com.codigoalvo.garage.dto

import br.com.codigoalvo.garage.annotation.NoArg
import java.time.LocalDateTime

@NoArg
data class SpotStatusResponse(
    val occupied: Boolean,
    val entryTime: LocalDateTime?,
    val timeParked: LocalDateTime?
)
