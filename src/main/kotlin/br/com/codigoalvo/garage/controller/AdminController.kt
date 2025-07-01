package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.dto.GarageConfigRequest
import br.com.codigoalvo.garage.domain.enums.MessageKey
import br.com.codigoalvo.garage.model.ApiResponse
import br.com.codigoalvo.garage.model.ApiResponseFactory
import br.com.codigoalvo.garage.service.GarageSetupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(
    private val garageSetupService: GarageSetupService,
    private val apiResponseFactory: ApiResponseFactory,
) {
    @PostMapping("/setup")
    fun setupGarage(@RequestBody garageConfigRequest: GarageConfigRequest): ResponseEntity<ApiResponse<Nothing>> {
        val responseData = garageSetupService.initializeGarage(garageConfigRequest)
        return apiResponseFactory.prepareResponseEntity(
            localizationKey = MessageKey.GARAGE_SETUP_SUCCESS,
            message = "Garage setup completed successfully: ${responseData["sectors"]} sectors and ${responseData["spots"]} spots.",
            metadata = responseData
        )
    }
}
