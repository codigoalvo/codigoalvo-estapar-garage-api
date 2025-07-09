package br.com.codigoalvo.garage.domain.enums

enum class MessageKey(val key: String) {

    GARAGE_SETUP_SUCCESS("setup.success.garage"),
    WEBHOOK_PROCESSED_SUCCESS("webhook.success.processed"),

    ERROR_VALIDATION_FIELDS("validation.error.fields"),
    ERROR_INTERNAL_SERVER("server.error.internal"),
    ERROR_VALIDATION_REQUEST_DATA("validation.error.request.data"),
    ERROR_INVALID_SYSTEM_STATE("system.error.state.invalid")
    ;

    override fun toString() = key
}