package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.dto.SpotStatusRequest
import br.com.codigoalvo.garage.dto.SpotStatusResponse
import br.com.codigoalvo.garage.service.SpotStatusService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/spot-status")
class SpotStatusController(
    private val service: SpotStatusService
) {

    @PostMapping
    fun getSpotStatus(@RequestBody request: SpotStatusRequest): SpotStatusResponse {
        return service.getStatus(request)
    }

}
