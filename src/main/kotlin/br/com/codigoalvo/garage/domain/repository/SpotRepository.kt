package br.com.codigoalvo.garage.domain.repository

import br.com.codigoalvo.garage.domain.model.Spot
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpotRepository : JpaRepository<Spot, UUID>
