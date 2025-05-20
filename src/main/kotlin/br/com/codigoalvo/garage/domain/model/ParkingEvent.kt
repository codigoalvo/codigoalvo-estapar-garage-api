package br.com.codigoalvo.garage.domain.model

import br.com.codigoalvo.garage.domain.enums.EventType
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "parking_event")
data class ParkingEvent(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(name = "license_plate", nullable = false, length = 20)
    val licensePlate: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 10)
    val eventType: EventType,

    @Column(name = "event_time")
    val eventTime: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    val spot: Spot? = null,

    )

