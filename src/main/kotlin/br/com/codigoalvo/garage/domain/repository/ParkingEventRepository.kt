package br.com.codigoalvo.garage.domain.repository

import br.com.codigoalvo.garage.domain.model.ParkingEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ParkingEventRepository : JpaRepository<ParkingEvent, UUID>
