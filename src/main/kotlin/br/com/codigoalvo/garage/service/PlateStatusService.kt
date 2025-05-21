package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.domain.repository.ParkingEventRepository
import br.com.codigoalvo.garage.dto.PlateStatusRequest
import br.com.codigoalvo.garage.dto.PlateStatusResponse
import br.com.codigoalvo.garage.domain.enums.EventType.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class PlateStatusService(
    private val parkingEventRepository: ParkingEventRepository,
    private val pricingRuleService: PricingRuleService
) {

    fun getStatus(request: PlateStatusRequest): PlateStatusResponse {
        val plate = request.licensePlate

        val entry = parkingEventRepository.findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(plate, ENTRY)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Placa '$plate' não encontrada")
        val parked = parkingEventRepository.findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(plate, PARKED)
        val exit = parkingEventRepository.findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(plate, EXIT)

        val entryTime = entry?.eventTime

        requireNotNull(entryTime) { "Não foi encontrado evento de entrada para a placa $plate" }

        val parkedTime = parked?.eventTime
        val exitTime = exit?.eventTime ?: LocalDateTime.now(ZoneOffset.UTC)

        val durationMinutes = pricingRuleService.calculateTimePeriodInMinutes(entryTime, exitTime)
        val durationMultiplier = pricingRuleService.calculateDurationMultiplier(durationMinutes)

        val spot = parked?.spot
        val sector = spot?.sector
        val basePrice = sector?.basePrice ?: BigDecimal.ZERO
        val occupancyRate:Double = sector?.let { pricingRuleService.calculateOccupancyRate(it) } ?: 0.0
        val occupancyMultiplier = pricingRuleService.calculateOccupancyMultiplier(occupancyRate)

        val price = pricingRuleService.calculateCharge(
                basePrice = basePrice,
                durationMultiplier = durationMultiplier,
                occupancyMultiplier = occupancyMultiplier
            )

        return PlateStatusResponse(
            licensePlate = plate,
            priceUntilNow = price,
            entryTime = entryTime,
            timeParked = parkedTime
        )
    }

}
