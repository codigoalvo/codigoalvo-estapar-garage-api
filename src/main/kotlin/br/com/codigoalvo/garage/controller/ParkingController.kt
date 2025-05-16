package br.com.codigoalvo.garage.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/parking")
class ParkingController {

    private val logger = LoggerFactory.getLogger(ParkingController::class.java)

    @PostMapping("/spot-event")
    fun receiveSpotEvent(@RequestBody payload: String): ResponseEntity<String> {
        logger.info("Recebido evento de vaga: $payload")
        return ResponseEntity.ok("Evento recebido")
    }

    @PostMapping("/parking-event")
    fun receiveParkingEvent(@RequestBody payload: String): ResponseEntity<String> {
        logger.info("Recebido evento de estacionamento: $payload")
        return ResponseEntity.ok("Evento recebido")
    }

}
