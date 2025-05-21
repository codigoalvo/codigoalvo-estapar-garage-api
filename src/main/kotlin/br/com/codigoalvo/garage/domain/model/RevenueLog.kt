package br.com.codigoalvo.garage.domain.model

import jakarta.persistence.*
import jakarta.persistence.FetchType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Entity
@Table(name = "revenue_log")
class RevenueLog(

    @Id
    @GeneratedValue
    var id: UUID? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "entry_event_id")
    var entryEvent: ParkingEvent,

    @ManyToOne(optional = false)
    @JoinColumn(name = "parked_event_id")
    var parkedEvent: ParkingEvent,

    @ManyToOne(optional = false)
    @JoinColumn(name = "exit_event_id")
    var exitEvent: ParkingEvent,

    @Column(nullable = false)
    var referenceDate: LocalDate,

    @Column(nullable = false)
    var durationMinutes: Long,

    @Column(nullable = false, precision = 10, scale = 2)
    var basePrice: BigDecimal,

    @Column(nullable = false)
    var occupancyRate: Double,

    @Column(nullable = false, precision = 5, scale = 2)
    var occupancyMultiplier: BigDecimal,

    @Column(nullable = false, precision = 5, scale = 2)
    var periodMultiplier: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    var amountCharged: BigDecimal,

    @Column(nullable = false)
    var currency: String = "BRL",

    @Column(nullable = false)
    var timestamp: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)
)

