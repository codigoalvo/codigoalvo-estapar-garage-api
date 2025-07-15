package br.com.codigoalvo.garage.domain.repository

import br.com.codigoalvo.garage.domain.model.Sector
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SectorRepository : JpaRepository<Sector, UUID> {
    fun findByCode(code: String): Sector?
}
