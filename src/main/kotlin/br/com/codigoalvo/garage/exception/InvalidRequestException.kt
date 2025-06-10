package br.com.codigoalvo.garage.exception

import org.springframework.http.HttpStatus

class InvalidRequestException(
    message: String = "Invalid request data",
    cause: Throwable? = null,
    localizationKey: String = "error.invalid.request"
) : ApiException(message, HttpStatus.BAD_REQUEST, localizationKey, cause)