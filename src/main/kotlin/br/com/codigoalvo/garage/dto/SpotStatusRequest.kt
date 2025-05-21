package br.com.codigoalvo.garage.dto

import br.com.codigoalvo.garage.annotation.NoArg

@NoArg
data class SpotStatusRequest(
    val lat: Double,
    val lng: Double
)
