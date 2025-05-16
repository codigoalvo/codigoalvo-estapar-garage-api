package br.com.codigoalvo.garage.domain.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "sector")
class Sector(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(nullable = false)
    var name: String,

    @Column(name = "max_capacity", nullable = false)
    val capacity: Int,
)

