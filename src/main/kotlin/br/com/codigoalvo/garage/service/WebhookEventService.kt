package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.domain.enums.EventType
import br.com.codigoalvo.garage.domain.model.ParkingEvent
import br.com.codigoalvo.garage.domain.model.RevenueLog
import br.com.codigoalvo.garage.domain.model.Spot
import br.com.codigoalvo.garage.domain.repository.ParkingEventRepository
import br.com.codigoalvo.garage.domain.repository.RevenueLogRepository
import br.com.codigoalvo.garage.domain.repository.SpotRepository
import br.com.codigoalvo.garage.exception.InvalidRequestException
import br.com.codigoalvo.garage.exception.InvalidStateException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class WebhookEventService(
    private val objectMapper: ObjectMapper,
    private val spotRepository: SpotRepository,
    private val parkingEventRepository: ParkingEventRepository,
    private val revenueLogRepository: RevenueLogRepository,
    private val pricingRuleService: PricingRuleService,
) {

    private val logger = LoggerFactory.getLogger(WebhookEventService::class.java)

    @Transactional
    fun processRawEvent(payload: String): Any {
        val rootNode = objectMapper.readTree(payload)

        val eventTypeStr = rootNode["event_type"]?.asText()
            ?: throw InvalidRequestException("Campo 'event_type' ausente no payload.")

        val eventType = try {
            EventType.valueOf(eventTypeStr)
        } catch (ex: Exception) {
            throw InvalidRequestException("Tipo de evento desconhecido: '$eventTypeStr'")
        }

        val licensePlate = rootNode["license_plate"]?.asText()
            ?: throw InvalidRequestException("Campo 'license_plate' ausente no payload.")

        val eventTime = extractEventTime(rootNode, eventType)

        val spot = if (eventType == EventType.PARKED) {
            val lat = rootNode["lat"]?.asDouble()
            val lng = rootNode["lng"]?.asDouble()
            if (lat == null || lng == null) {
                throw InvalidRequestException("Coordenadas 'lat' e 'lng' são obrigatórias para eventos PARKED.")
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

        val eventSaved = parkingEventRepository.save(event)
        logger.info("Evento salvo com sucesso: $eventSaved")

        return if (eventType == EventType.PARKED && spot != null) {
            processParkedEvent(eventSaved, spot)
        } else if (eventType == EventType.EXIT) {
            processExitEvent(eventSaved)
        } else {
            eventSaved
        }

    }

    private fun extractEventTime(event: JsonNode, eventType: EventType): LocalDateTime? {
        return when (eventType) {
            EventType.ENTRY -> {
                val value = event["entry_time"]?.asText()
                    ?: throw InvalidRequestException("Evento ENTRY não contém entry_time")
                LocalDateTime.parse(value)
            }
            EventType.EXIT -> {
                val value = event["exit_time"]?.asText()
                    ?: throw InvalidRequestException("Evento EXIT não contém exit_time")
                LocalDateTime.parse(value)
            }
            EventType.PARKED -> null
        }
    }

    private fun findSpotByCoordinates(lat: Double, lng: Double): Spot {
        return spotRepository.findByLatitudeAndLongitude(lat, lng)
            ?: throw InvalidStateException("Vaga com lat=$lat e lng=$lng não encontrada.")
    }

    private fun processParkedEvent(event: ParkingEvent, spot: Spot): ParkingEvent {
        spot.isOccupied = true
        spotRepository.save(spot)
        logger.info("OCUPADO Spot [${spot.externalId}] com ID ${spot.id}")

        val occupancyRate = pricingRuleService.calculateOccupancyRate(spot.sector)

        event.occupancyRate = occupancyRate

        return parkingEventRepository.save(event)
    }

    private fun processExitEvent(exitEvent: ParkingEvent): RevenueLog {
        val entryEvent = parkingEventRepository.findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(
            exitEvent.licensePlate, EventType.ENTRY
        ) ?: throw InvalidStateException("Entrada não encontrada para a placa ${exitEvent.licensePlate}")

        val parkedEvent = parkingEventRepository.findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(
            exitEvent.licensePlate, EventType.PARKED
        ) ?: throw InvalidStateException("Evento PARKED não encontrado para a placa ${exitEvent.licensePlate}")

        val spot = parkedEvent.spot
            ?: throw InvalidStateException("Evento PARKED para ${exitEvent.licensePlate} sem spot associado")

        val sector = spot.sector
        val occupancyRate: Double = parkedEvent.occupancyRate ?: 0.0
        val basePrice = sector.basePrice

        val durationMinutes = pricingRuleService.calculateTimePeriodInMinutes(
            entryTime = entryEvent.eventTime,
            exitTime = exitEvent.eventTime,
        )

        logger.info("Calculando o tempo para a Placa: [${exitEvent.licensePlate}]")
        logger.info("-> Horário de entrada: ${entryEvent.eventTime}")
        logger.info("-> Horário de saída: ${exitEvent.eventTime}")
        logger.info("-> Duração em minutos: $durationMinutes")
        logger.info("< ENTRY event: $entryEvent")
        logger.info("* PARKED event: $parkedEvent")
        logger.info("> EXIT event: $exitEvent")

        val occupancyMultiplier = pricingRuleService.calculateOccupancyMultiplier(occupancyRate)
        val durationMultiplier = pricingRuleService.calculateDurationMultiplier(durationMinutes)
        val amount = pricingRuleService.calculateCharge(
            basePrice = basePrice,
            durationMultiplier = durationMultiplier,
            occupancyMultiplier = occupancyMultiplier
        )

        val revenueLog = RevenueLog(
            entryEvent = entryEvent,
            parkedEvent = parkedEvent,
            exitEvent = exitEvent,
            referenceDate = parkedEvent.eventTime?.toLocalDate() ?: LocalDate.now(ZoneOffset.UTC),
            durationMinutes = durationMinutes,
            periodMultiplier = durationMultiplier,
            occupancyMultiplier = occupancyMultiplier,
            basePrice = basePrice,
            occupancyRate = occupancyRate,
            amountCharged = amount
        )

        logger.info("Salvando RevenueLog: $revenueLog")
        val savedRevenueLog = revenueLogRepository.save(revenueLog)
        logger.info("Salvou com sucesso: $savedRevenueLog")

        val managedSpot = spotRepository.findById(spot.id)
            .orElseThrow { InvalidStateException("Spot com ID ${spot.id} não encontrado") }
        managedSpot.isOccupied = false
        spotRepository.save(managedSpot)
        logger.info("DESOCUPADO Spot [${spot.externalId}] com ID ${spot.id}")
        return revenueLog
    }

}
