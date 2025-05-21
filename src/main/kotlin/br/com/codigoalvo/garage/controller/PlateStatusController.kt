package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.dto.PlateStatusRequest
import br.com.codigoalvo.garage.dto.PlateStatusResponse
import br.com.codigoalvo.garage.service.PlateStatusService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/plate-status")
class PlateStatusController(
    private val service: PlateStatusService
) {

    @PostMapping
    fun getPlateStatus(@RequestBody request: PlateStatusRequest): PlateStatusResponse {
        return service.getStatus(request)
    }

}
