package br.com.codigoalvo.garage.dto

data class GarageConfigResponse(
    val garage: List<SectorConfigDto> = emptyList(),
    val spots: List<SpotConfigDto> = emptyList()
)
