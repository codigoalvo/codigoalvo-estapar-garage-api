package br.com.codigoalvo.garage.model

import br.com.codigoalvo.garage.domain.enums.MessageKey
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class ApiResponseFactory(
    @Value("\${api.version}") private val apiVersion: String,
    private val request: HttpServletRequest
) {
    fun <T> prepareResponse(
        message: String? = null,
        data: T? = null,
        status: HttpStatus = HttpStatus.OK
    ): ApiResponse<T> {
        return ApiResponse.Builder<T>()
            .status(status)
            .message(message)
            .version(apiVersion)
            .path(request.requestURI)
            .data(data)
            .build()
    }

    fun <T> prepareResponseEntity(
        message: String? = null,
        data: T? = null,
        status: HttpStatus = HttpStatus.OK,
        metadata: Map<String, Any> = emptyMap(),
        localizationKey: MessageKey? = null
    ): ResponseEntity<ApiResponse<T>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.Builder<T>()
                .status(status)
                .message(message)
                .localizationKey(localizationKey?.key)
                .version(apiVersion)
                .path(request.requestURI)
                .metadata(metadata)
                .data(data)
                .build()
        )
    }
}
