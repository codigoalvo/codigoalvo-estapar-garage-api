package br.com.codigoalvo.garage.domain.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalTime
import java.util.*

@Entity
@Table(name = "sector")
data class Sector(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val code: String,  // "A", "B", etc

    @Column(name = "base_price", nullable = false)
    val basePrice: BigDecimal,

    @Column(name = "max_capacity", nullable = false)
    val capacity: Int,

    @Column(name = "open_hour", nullable = false)
    val openHour: LocalTime,

    @Column(name = "close_hour", nullable = false)
    val closeHour: LocalTime,

    @Column(name = "duration_limit_minutes", nullable = false)
    val durationLimitMinutes: Int,

    @JsonIgnore
    @OneToMany(mappedBy = "sector", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val spots: MutableList<Spot> = mutableListOf()


) {
    override fun toString(): String {
        return "Sector(id=$id, code='$code', basePrice=$basePrice, capacity=$capacity, openHour=$openHour, " +
                "closeHour=$closeHour, durationLimitMinutes=$durationLimitMinutes)"
    }
}