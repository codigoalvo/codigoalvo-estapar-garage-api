package br.com.codigoalvo.garage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class WebhookEventDto(
    @JsonProperty("event_type")
    val eventType: String,

    @JsonProperty("license_plate")
    val licensePlate: String,

    @JsonProperty("entry_time")
    val entryTime: LocalDateTime? = null,

    @JsonProperty("exit_time")
    val exitTime: LocalDateTime? = null,

    val lat: Double? = null,
    val lng: Double? = null
)