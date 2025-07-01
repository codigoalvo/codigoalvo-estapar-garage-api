package br.com.codigoalvo.garage.exception

import br.com.codigoalvo.garage.domain.enums.MessageKey
import br.com.codigoalvo.garage.model.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.UUID

@RestControllerAdvice
class GlobalExceptionHandler(
    private val request: HttpServletRequest,
    @Value("\${api.debug.enabled}") private val debugEnabled: Boolean,
    @Value("\${api.version}") private val apiVersion: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.Builder<Nothing>()
            .status(ex.httpStatus)
            .message(ex.message)
            .version(apiVersion)
            .localizationKey(ex.localizationKey)
            .path(request.requestURI)
            .errorDetails(
                ApiResponse.ErrorDetails(
                    cause = ex.cause?.message,
                    stackTrace = if (debugEnabled) ex.stackTraceToString().lines() else null,
                    validationErrors = null
                )
            )
            .build()

        logError(response.id, ex)
        return ResponseEntity.status(ex.httpStatus).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errors = ex.bindingResult.fieldErrors.associate { error ->
            error.field to (error.defaultMessage ?: "Invalid value")
        }

        val response = ApiResponse.Builder<Nothing>()
            .status(HttpStatus.BAD_REQUEST)
            .message("Validation failed")
            .version(apiVersion)
            .localizationKey(MessageKey.ERROR_VALIDATION_FIELDS.key)
            .path(request.requestURI)
            .errorDetails(
                ApiResponse.ErrorDetails(
                    cause = "Invalid request parameters",
                    stackTrace = if (debugEnabled) ex.stackTraceToString().lines() else null,
                    validationErrors = errors
                )
            )
            .build()

        logError(response.id, ex)
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val response = ApiResponse.Builder<Nothing>()
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .message("Internal server error")
            .version(apiVersion)
            .localizationKey(MessageKey.ERROR_INTERNAL_SERVER.key)
            .path(request.requestURI)
            .errorDetails(
                ApiResponse.ErrorDetails(
                    cause = ex.message,
                    stackTrace = if (debugEnabled) ex.stackTraceToString().lines() else null,
                    validationErrors = null
                )
            )
            .build()

        logError(response.id, ex)
        return ResponseEntity.internalServerError().body(response)
    }

    private fun logError(id: UUID?, ex: Throwable) {
        logger.error(
            "Error ID: $id | Path: ${request.requestURI} | Type: ${ex.javaClass.simpleName}",
            ex
        )
    }
}
