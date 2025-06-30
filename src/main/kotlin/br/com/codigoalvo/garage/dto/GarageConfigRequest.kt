package br.com.codigoalvo.garage.dto

data class GarageConfigRequest(
    val garage: List<SectorConfigRequest> = emptyList(),
    val spots: List<SpotConfigRequest> = emptyList()
)
