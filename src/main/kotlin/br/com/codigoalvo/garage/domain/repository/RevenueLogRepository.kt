package br.com.codigoalvo.garage.domain.repository

import br.com.codigoalvo.garage.domain.model.RevenueLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

interface RevenueLogRepository : JpaRepository<RevenueLog, UUID> {

    @Query("""
        SELECT COALESCE(SUM(r.amountCharged), 0)
        FROM RevenueLog r
        WHERE r.referenceDate = :referenceDate
          AND r.parkedEvent.spot.sector.code = :sector
    """)
    fun sumAmountChargedBySectorAndReferenceDate(
        @Param("sector") sector: String,
        @Param("referenceDate") referenceDate: LocalDate
    ): BigDecimal


}
