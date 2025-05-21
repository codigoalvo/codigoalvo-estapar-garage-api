package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.domain.repository.RevenueLogRepository
import br.com.codigoalvo.garage.dto.RevenueRequest
import br.com.codigoalvo.garage.dto.RevenueResponse
import org.springframework.stereotype.Service

@Service
class RevenueService(
    private val revenueLogRepository: RevenueLogRepository
) {

    fun calculateRevenue(request: RevenueRequest): RevenueResponse {
        val total = revenueLogRepository.sumAmountChargedBySectorAndReferenceDate(
            sector = request.sector,
            referenceDate = request.date
        )
        return RevenueResponse(
            amount = total,
            timestamp = request.date.atStartOfDay()
        )
    }
}
