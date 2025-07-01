package br.com.codigoalvo.garage.exception

import br.com.codigoalvo.garage.domain.enums.MessageKey
import org.springframework.http.HttpStatus

class InvalidRequestException(
    message: String = "Invalid request data",
    cause: Throwable? = null,
    localizationKey: String = MessageKey.ERROR_VALIDATION_REQUEST_DATA.key
) : ApiException(message, HttpStatus.BAD_REQUEST, localizationKey, cause)