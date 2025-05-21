package br.com.codigoalvo.garage.domain.repository

import br.com.codigoalvo.garage.domain.enums.EventType
import br.com.codigoalvo.garage.domain.model.ParkingEvent
import br.com.codigoalvo.garage.domain.model.Spot
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ParkingEventRepository : JpaRepository<ParkingEvent, UUID> {
    fun findTopByLicensePlateAndEventTypeOrderByEventTimeDesc(licensePlate: String, eventType: EventType): ParkingEvent?
    fun findTopBySpotAndEventTypeOrderByEventTimeDesc(spot: Spot, eventType: EventType): ParkingEvent?
}
