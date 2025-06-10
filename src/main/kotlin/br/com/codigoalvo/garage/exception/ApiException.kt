package br.com.codigoalvo.garage.exception

import org.springframework.http.HttpStatus

open class ApiException(
    message: String,
    val httpStatus: HttpStatus,
    val localizationKey: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)