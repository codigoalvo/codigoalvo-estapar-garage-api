package br.com.codigoalvo.garage.domain.repository

import br.com.codigoalvo.garage.domain.model.Sector
import br.com.codigoalvo.garage.domain.model.Spot
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpotRepository : JpaRepository<Spot, UUID> {
    fun findByLatitudeAndLongitude(lat: Double, lng: Double): Spot

    fun countBySector(sector: Sector): Long
    fun countOccupiedSpotsBySector(sector: Sector): Long

}
