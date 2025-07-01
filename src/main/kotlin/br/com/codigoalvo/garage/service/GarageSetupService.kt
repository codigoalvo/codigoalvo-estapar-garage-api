package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.domain.repository.SectorRepository
import br.com.codigoalvo.garage.domain.repository.SpotRepository
import br.com.codigoalvo.garage.domain.model.Sector
import br.com.codigoalvo.garage.domain.model.Spot
import br.com.codigoalvo.garage.dto.GarageConfigRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class GarageSetupService(
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository,
    private val restTemplate: RestTemplate,
    private var objectMapper: ObjectMapper,
    @Value("\${garage.simulator.url}") private val simulatorUrl: String
) {

    private val logger = LoggerFactory.getLogger(GarageSetupService::class.java)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun initializeGarage(garageConfig: GarageConfigRequest? = null): Map<String, Any> {

        val garageConfigRequest: GarageConfigRequest = garageConfig
            ?: requestAdminSetup("$simulatorUrl/garage")

        logger.info("Garage config (JSON): ${objectMapper.writeValueAsString(garageConfigRequest)}")

        spotRepository.deleteAllInBatch()
        sectorRepository.deleteAllInBatch()

        val sectors = garageConfigRequest.garage.map {
            Sector(
                id = UUID.randomUUID(),
                code = it.sector,
                capacity = it.maxCapacity,
                basePrice = it.basePrice,
                openHour = LocalTime.parse(it.openHour, timeFormatter),
                closeHour = LocalTime.parse(it.closeHour, timeFormatter),
                durationLimitMinutes = it.durationLimitMinutes
            )
        }
        val savedSectors = sectorRepository.saveAll(sectors)

        val spotEntities = garageConfigRequest.spots.map {
            Spot(
                id = UUID.randomUUID(),
                externalId = it.id,
                latitude = it.lat,
                longitude = it.lng,
                isOccupied = it.occupied,
                sector = sectors.first { sector -> sector.code == it.sector }
            )
        }

        logger.info("Configuração recebida: $spotEntities")

        val savedSpots = spotRepository.saveAll(spotEntities)
        logger.info("Setup da garagem concluído com sucesso: ${savedSectors.size} setores e ${savedSpots.size} vagas.")

        return mapOf(
            "sectors" to savedSectors.size,
            "spots" to savedSpots.size,
        )
    }

    private fun requestAdminSetup(url: String = "$simulatorUrl/garage"): GarageConfigRequest {
        logger.info("Iniciando setup da garagem com a chamada em: $url")
        return restTemplate.getForObject<GarageConfigRequest>("$url")
    }

}
