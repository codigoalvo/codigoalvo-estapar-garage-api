package br.com.codigoalvo.garage.exception

import org.springframework.http.HttpStatus

class InvalidStateException(
    message: String = "Invalid system state",
    cause: Throwable? = null,
    localizationKey: String = "error.invalid.state"
) : ApiException(message, HttpStatus.CONFLICT, localizationKey, cause)