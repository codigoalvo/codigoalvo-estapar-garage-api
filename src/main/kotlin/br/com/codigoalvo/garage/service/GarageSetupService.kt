package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.dto.SectorConfigDto
import br.com.codigoalvo.garage.dto.SpotConfigDto
import br.com.codigoalvo.garage.domain.repository.SectorRepository
import br.com.codigoalvo.garage.domain.repository.SpotRepository
import br.com.codigoalvo.garage.domain.model.Sector
import br.com.codigoalvo.garage.domain.model.Spot
import br.com.codigoalvo.garage.dto.GarageConfigResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
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
    @Value("\${garage.simulator.url}") private val simulatorUrl: String
) {

    private val logger = LoggerFactory.getLogger(GarageSetupService::class.java)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun initializeGarage() {
        logger.info("Iniciando setup da garagem com a chamada em: $simulatorUrl/garage")

        val response = restTemplate.getForObject<GarageConfigResponse>("$simulatorUrl/garage")
            ?: throw IllegalStateException("Não foi possível obter a configuração da garagem.")

        // Limpa dados anteriores
        spotRepository.deleteAllInBatch()
        sectorRepository.deleteAllInBatch()

        // Persiste setores
        val sectors = response.garage.map {
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
        sectorRepository.saveAll(sectors)

        // Persiste vagas
        val spotEntities = response.spots.map {
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

        spotRepository.saveAll(spotEntities)

        logger.info("Setup da garagem concluído com sucesso: ${sectors.size} setores e ${spotEntities.size} vagas.")
    }

}
