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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    val event: ParkingEvent,

    @Column(name = "reference_date", nullable = false)
    val referenceDate: LocalDate,

    @Column(name = "duration_minutes", nullable = false)
    val durationMinutes: Long,

    @Column(name = "amount_charged", nullable = false)
    val amountCharged: BigDecimal,

    @Column(name = "occupancy_rate", nullable = false)
    val occupancyRate: Double,

    @Column(nullable = false, length = 3)
    val currency: String = "BRL",

    @Column(nullable = false)
    val timestamp: OffsetDateTime = OffsetDateTime.now()
)
