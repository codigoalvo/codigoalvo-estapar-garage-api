package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.service.WebhookEventService
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhook")
class WebhookController(
    private val eventService: WebhookEventService,
) {

    private val logger: Logger = LoggerFactory.getLogger(WebhookController::class.java)

    @PostMapping
    fun receiveEvent(@RequestBody rawPayload: String): ResponseEntity<String> {
        logger.info("[*] Webhook recebido: $rawPayload")
        eventService.processRawEvent(rawPayload)
        return ResponseEntity.ok().body<String>("Evento processado com sucesso")
    }

}