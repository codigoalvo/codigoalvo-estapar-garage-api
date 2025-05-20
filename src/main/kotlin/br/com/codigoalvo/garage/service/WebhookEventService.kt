package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.domain.enums.EventType
import br.com.codigoalvo.garage.domain.model.ParkingEvent
import br.com.codigoalvo.garage.domain.model.Spot
import br.com.codigoalvo.garage.domain.repository.ParkingEventRepository
import br.com.codigoalvo.garage.domain.repository.SpotRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import java.util.*

@Service
class WebhookEventService(
    private val objectMapper: ObjectMapper,
    private val spotRepository: SpotRepository,
    private val parkingEventRepository: ParkingEventRepository
) {

    private val logger = LoggerFactory.getLogger(WebhookEventService::class.java)

    @Transactional
    fun processRawEvent(payload: String) {
        val rootNode = objectMapper.readTree(payload)

        val eventTypeStr = rootNode["event_type"]?.asText()
            ?: throw IllegalArgumentException("Campo 'event_type' ausente no payload.")

        val eventType = try {
            EventType.valueOf(eventTypeStr)
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException("Tipo de evento desconhecido: '$eventTypeStr'")
        }

        val licensePlate = rootNode["license_plate"]?.asText()
            ?: throw IllegalArgumentException("Campo 'license_plate' ausente no payload.")

        val eventTime = extractEventTime(rootNode, eventType)

        val spot = if (eventType == EventType.PARKED) {
            val lat = rootNode["lat"]?.asDouble()
            val lng = rootNode["lng"]?.asDouble()
            if (lat == null || lng == null) {
                throw IllegalArgumentException("Coordenadas 'lat' e 'lng' são obrigatórias para eventos PARKED.")
            }
            findSpotByCoordinates(lat, lng)
        } else {
            null
        }

        val event = ParkingEvent(
            licensePlate = licensePlate,
            eventType = eventType,
            eventTime = eventTime,
            spot = spot
        )

        parkingEventRepository.save(event)

        logger.info("Evento salvo com sucesso: $event")
    }

    private fun extractEventTime(event: JsonNode, eventType: EventType): LocalDateTime {
        return when (eventType) {
            EventType.ENTRY -> {
                val value = event["entry_time"]?.asText()
                    ?: throw IllegalArgumentException("Evento ENTRY não contém entry_time")
                LocalDateTime.parse(value)
            }
            EventType.EXIT -> {
                val value = event["exit_time"]?.asText()
                    ?: throw IllegalArgumentException("Evento EXIT não contém exit_time")
                LocalDateTime.parse(value)
            }
            EventType.PARKED -> LocalDateTime.now()
        }
    }



    private fun findSpotByCoordinates(lat: Double, lng: Double): Spot {
        return spotRepository.findByLatitudeAndLongitude(lat, lng)
            ?: throw IllegalStateException("Vaga com lat=$lat e lng=$lng não encontrada.")
    }
}
