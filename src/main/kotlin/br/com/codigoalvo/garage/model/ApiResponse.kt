package br.com.codigoalvo.garage.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T> private constructor(
    val id: UUID,
    val timestamp: Instant,
    val status: Int,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val localizationKey: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val path: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val version: String?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val metadata: Map<String, Any>,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val errorDetails: ErrorDetails?
) {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class ErrorDetails(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        val cause: String? = null,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        val stackTrace: List<String>? = null,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val validationErrors: Map<String, String>? = null
    ) {
        fun isEmpty(): Boolean {
            return cause == null && stackTrace == null && validationErrors.isNullOrEmpty()
        }
    }

    class Builder<T> {
        private var id: UUID = UUID.randomUUID()
        private var timestamp: Instant = Instant.now()
        private lateinit var status: HttpStatus
        private var message: String? = null
        private var localizationKey: String? = null
        private var path: String? = null
        private var version: String? = null
        private var metadata: Map<String, Any> = emptyMap()
        private var data: T? = null
        private var errorDetails: ErrorDetails? = null

        fun status(status: HttpStatus) = apply { this.status = status }
        fun message(message: String?) = apply { this.message = message }
        fun localizationKey(key: String?) = apply { this.localizationKey = key }
        fun path(path: String?) = apply { this.path = path }
        fun version(version: String?) = apply { this.version = version }
        fun metadata(metadata: Map<String, Any>) = apply { this.metadata = metadata }
        fun data(data: T?) = apply { this.data = data }

        fun errorDetails(errorDetails: ErrorDetails?) = apply {
            this.errorDetails = errorDetails.takeUnless { it?.isEmpty() ?: true }
        }

        fun build() = ApiResponse(
            id = id,
            timestamp = timestamp,
            status = status.value(),
            message = message,
            localizationKey = localizationKey,
            version = version,
            path = path,
            metadata = metadata,
            data = data,
            errorDetails = errorDetails
        )
    }

    companion object {
        fun <T> success(data: T? = null) = Builder<T>()
            .status(HttpStatus.OK)
            .data(data)
    }

}