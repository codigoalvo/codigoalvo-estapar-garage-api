package br.com.codigoalvo.garage.domain.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "revenue_log")
data class RevenueLog(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    val sector: Sector,

    @Column(name = "reference_date", nullable = false)
    val referenceDate: LocalDate,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false, length = 3)
    val currency: String = "BRL",

    @Column(nullable = false)
    val timestamp: OffsetDateTime = OffsetDateTime.now()
)
