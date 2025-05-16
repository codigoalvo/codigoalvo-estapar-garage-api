package br.com.codigoalvo.garage.domain.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "spot")
data class Spot(

    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "spot_number", nullable = false)
    val code: String,

    @Column(name = "is_occupied")
    val isOccupied: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "sector_id")
    val sector: Sector
)
