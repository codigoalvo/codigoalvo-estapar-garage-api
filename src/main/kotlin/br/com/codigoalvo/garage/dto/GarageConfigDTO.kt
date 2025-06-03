package br.com.codigoalvo.garage.dto

data class GarageConfigDTO(
    val garage: List<SectorConfigDto> = emptyList(),
    val spots: List<SpotConfigDto> = emptyList()
)
