package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.config.PricingProperties
import br.com.codigoalvo.garage.domain.enums.EventType
import br.com.codigoalvo.garage.domain.model.ParkingEvent
import br.com.codigoalvo.garage.domain.model.RevenueLog
import br.com.codigoalvo.garage.domain.model.Spot
import br.com.codigoalvo.garage.domain.repository.ParkingEventRepository
import br.com.codigoalvo.garage.domain.repository.RevenueLogRepository
import br.com.codigoalvo.garage.domain.repository.SpotRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class WebhookEventService(
    private val objectMapper: ObjectMapper,
    private val spotRepository: SpotRepository,
    private val parkingEventRepository: ParkingEventRepository,
    private val revenueLogRepository: RevenueLogRepository,
    private val pricingProperties: PricingProperties,
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

        if (eventType == EventType.PARKED && spot != null) {
            spot.isOccupied = true
            spotRepository.save(spot)
        } else if (eventType == EventType.EXIT) {
            processExitEvent(event)
        }

        logger.info("Evento salvo com sucesso: $event")
    }

    private fun extractEventTime(event: JsonNode, eventType: EventType): LocalDateTime? {
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
            EventType.PARKED -> null
        }
    }

    private fun findSpotByCoordinates(lat: Double, lng: Double): Spot {
        return spotRepository.findByLatitudeAndLongitude(lat, lng)
            ?: throw IllegalStateException("Vaga com lat=$lat e lng=$lng não encontrada.")
    }

    @Transactional
    fun processExitEvent(exitEvent: ParkingEvent) {
        val entryEvent = parkingEventRepository.findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(
            exitEvent.licensePlate, EventType.ENTRY
        ) ?: throw IllegalStateException("Entrada não encontrada para a placa ${exitEvent.licensePlate}")

        val parkedEvent = parkingEventRepository.findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(
            exitEvent.licensePlate, EventType.PARKED
        ) ?: throw IllegalStateException("Evento PARKED não encontrado para a placa ${exitEvent.licensePlate}")

        val spot = parkedEvent.spot
            ?: throw IllegalStateException("Evento PARKED para ${exitEvent.licensePlate} sem spot associado")

        val sector = spot.sector
        val totalSpots = sector.spots.size
        val capacity = sector.capacity
        val occupiedSpots = sector.spots.count { it.isOccupied }
        val occupancyRate = occupiedSpots.toDouble() / capacity
        logger.info("Ocupação do setor [${sector.code}]")
        logger.info("-> Capacidade de vagas: $capacity")
        logger.info("-> Total de vagas: $totalSpots")
        logger.info("-> Vagas ocupadas: $occupiedSpots")
        logger.info("-> Taxa de ocupação: $occupancyRate")

        val durationMinutes = maxOf(Duration.between(entryEvent.eventTime, exitEvent.eventTime).toMinutes(), 1)
        logger.info("Calculando o tempo para a Placa: [${exitEvent.licensePlate}]")
        logger.info("-> Horário de entrada: ${entryEvent.eventTime}")
        logger.info("-> Horário de saída: ${exitEvent.eventTime}")
        logger.info("-> Duração em minutos: $durationMinutes")

        logger.info("< ENTRY event: $entryEvent")
        logger.info("* PARKED event: $parkedEvent")
        logger.info("> EXIT event: $exitEvent")

        val amount = calculateCharge(sector.basePrice, durationMinutes, occupancyRate)

        val revenueLog = RevenueLog(
            event = parkedEvent,
            referenceDate = parkedEvent.eventTime?.toLocalDate() ?: LocalDate.now(ZoneOffset.UTC),
            durationMinutes = durationMinutes,
            amountCharged = amount,
            occupancyRate = occupancyRate
        )

        revenueLogRepository.save(revenueLog)

        val occupiedSpot = parkedEvent.spot
        occupiedSpot.let {
            it.isOccupied = false
            spotRepository.save(it)
        }
    }

    private fun calculateCharge(
        basePrice: BigDecimal,
        durationMinutes: Long,
        occupancyRate: Double,
    ): BigDecimal {

        val multiplier = pricingProperties.rules
            .sortedBy { it.threshold }
            .firstOrNull { occupancyRate <= it.threshold }
            ?.multiplier ?: 1.0

        logger.info("Calculando cobrança:")
        logger.info("-> Duração: $durationMinutes minutos")
        logger.info("-> Taxa de Ocupação: $occupancyRate")
        logger.info("-> Base price: $basePrice")
        logger.info("-> Multiplicador aplicado: $multiplier")

        val hoursToCharge = if (durationMinutes <= 60) {
            BigDecimal.ONE
        } else {
            val extraMinutes = durationMinutes - 60
            val extraQuarters = Math.ceil(extraMinutes / 15.0).toInt()
            BigDecimal.ONE + (BigDecimal(extraQuarters) * BigDecimal("0.25"))
        }

        val finalAmount = basePrice.multiply(BigDecimal(multiplier)).multiply(hoursToCharge)
        logger.info("-> Valor final calculado: $finalAmount")

        return finalAmount
    }


}
