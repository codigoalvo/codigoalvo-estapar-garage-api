package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.domain.enums.MessageKey
import br.com.codigoalvo.garage.model.ApiResponse
import br.com.codigoalvo.garage.model.ApiResponseFactory
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
    private val apiResponseFactory: ApiResponseFactory,
) {

    private val logger: Logger = LoggerFactory.getLogger(WebhookController::class.java)

    @PostMapping
    fun receiveEvent(@RequestBody rawPayload: String): ResponseEntity<ApiResponse<Any>> {
        logger.info("[*] Webhook recebido: $rawPayload")
        val responseData = eventService.processRawEvent(rawPayload)
        return apiResponseFactory.prepareResponseEntity(
            localizationKey = MessageKey.WEBHOOK_PROCESSED_SUCCESS,
            message = "Evento processado com sucesso",
            data = responseData
        )
    }

}