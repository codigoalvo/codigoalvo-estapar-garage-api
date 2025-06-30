package br.com.codigoalvo.garage.dto

data class SpotConfigRequest(
    val id: Long = 0,
    val sector: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val occupied: Boolean = false
)