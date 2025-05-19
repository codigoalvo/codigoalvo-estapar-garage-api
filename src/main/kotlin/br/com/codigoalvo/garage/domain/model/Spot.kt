package br.com.codigoalvo.garage.domain.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "spot")
data class Spot(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "external_id", nullable = false, unique = true)
    val externalId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    val sector: Sector,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(name = "is_occupied", nullable = false)
    val isOccupied: Boolean = false
)
