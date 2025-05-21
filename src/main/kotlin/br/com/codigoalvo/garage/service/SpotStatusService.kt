package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.dto.SpotStatusRequest
import br.com.codigoalvo.garage.dto.SpotStatusResponse
import br.com.codigoalvo.garage.domain.repository.ParkingEventRepository
import br.com.codigoalvo.garage.domain.repository.SpotRepository
import br.com.codigoalvo.garage.domain.enums.EventType.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class SpotStatusService(
    private val spotRepository: SpotRepository,
    private val parkingEventRepository: ParkingEventRepository
) {

    fun getStatus(request: SpotStatusRequest): SpotStatusResponse {
        val spot = spotRepository.findByLatitudeAndLongitude(request.lat, request.lng)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Vaga não encontrada para latitude ${request.lat} e longitude ${request.lng}")

        val parked = parkingEventRepository.findTopBySpotAndEventTypeOrderByEventTimeDesc(spot, PARKED)
        val entry = parked?.licensePlate?.let {
            parkingEventRepository.findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(it, ENTRY)
        }

        return SpotStatusResponse(
            occupied = spot.isOccupied,
            entryTime = entry?.eventTime,
            timeParked = parked?.eventTime
        )
    }
}
