package br.com.codigoalvo.garage.controller

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/teste")
class WebhookController(
    private val logger: Logger = LoggerFactory.getLogger(WebhookController::class.java)
) {

    @PostMapping
    fun receiveEvent(@RequestBody body: String): ResponseEntity<Void> {
        logger.info("[*] Webhook recebido: $body")
        return ResponseEntity.ok().build()
    }

    @PostConstruct
    fun init() {
        logger.info("[!] WebhookController carregado com sucesso!")
    }

}