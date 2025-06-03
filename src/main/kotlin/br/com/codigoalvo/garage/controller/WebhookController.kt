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
        return try {
            eventService.processRawEvent(rawPayload)
            ResponseEntity.ok().build()
        } catch (ex: IllegalArgumentException) {
            logger.warn("IllegalArgumentException ao processar webhook: ${ex.message}", ex)
            ResponseEntity.badRequest().body<String>(ex.message)
        } catch (ex: IllegalStateException) {
            logger.warn("IllegalStateException ao processar webhook: ${ex.message}", ex)
            ResponseEntity.badRequest().body<String>(ex.message)
        } catch (ex: Exception) {
            logger.error("Exception ao processar webhook: ${ex.message}", ex)
            ResponseEntity.internalServerError().body<String>(ex.message)
        }
    }

}