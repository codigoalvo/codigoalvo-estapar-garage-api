package br.com.codigoalvo.garage.exception

import br.com.codigoalvo.garage.domain.enums.MessageKey
import org.springframework.http.HttpStatus

class InvalidStateException(
    message: String = "Invalid system state",
    cause: Throwable? = null,
    localizationKey: String = MessageKey.ERROR_INVALID_SYSTEM_STATE.key
) : ApiException(message, HttpStatus.CONFLICT, localizationKey, cause)