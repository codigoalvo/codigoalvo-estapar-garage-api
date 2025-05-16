package br.com.codigoalvo.garage.domain.repository

import br.com.codigoalvo.garage.domain.model.RevenueLog
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RevenueLogRepository : JpaRepository<RevenueLog, UUID>
