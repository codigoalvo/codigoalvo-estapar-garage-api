package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.dto.GarageConfigRequest
import br.com.codigoalvo.garage.model.ApiResponse
import br.com.codigoalvo.garage.service.GarageSetupService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(
    private val garageSetupService: GarageSetupService,
    private val request: HttpServletRequest,
    @Value("\${api.version}") private val apiVersion: String
) {
    @PostMapping("/setup")
    fun setupGarage(@RequestBody garageConfigRequest : GarageConfigRequest): ResponseEntity<ApiResponse<Nothing>> {
        val responseMessage  = garageSetupService.initializeGarage(garageConfigRequest)
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.Builder<Nothing>()
                .status(HttpStatus.OK)
                .message(responseMessage)
                .path(request.requestURI)
                .version(apiVersion)
                .build()
        )
    }
}
