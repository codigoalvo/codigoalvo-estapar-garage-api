package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.dto.GarageConfigDTO
import br.com.codigoalvo.garage.service.GarageSetupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(
    private val garageSetupService: GarageSetupService
) {
    @PostMapping("/setup")
    fun setupGarage(@RequestBody garageConfigDTO : GarageConfigDTO): ResponseEntity<Void> {
        garageSetupService.initializeGarage(garageConfigDTO)
        return ResponseEntity.ok().build()
    }
}
