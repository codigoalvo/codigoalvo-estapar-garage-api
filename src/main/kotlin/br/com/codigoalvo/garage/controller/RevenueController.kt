package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.dto.RevenueRequest
import br.com.codigoalvo.garage.dto.RevenueResponse
import br.com.codigoalvo.garage.service.RevenueService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/revenue")
class RevenueController(
    private val service: RevenueService
) {

    @PostMapping
    fun getRevenue(@RequestBody request: RevenueRequest): RevenueResponse {
        return service.calculateRevenue(request)
    }

}