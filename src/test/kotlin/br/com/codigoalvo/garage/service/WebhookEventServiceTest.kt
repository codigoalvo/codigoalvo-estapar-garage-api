package br.com.codigoalvo.garage.service

import br.com.codigoalvo.garage.domain.enums.EventType
import br.com.codigoalvo.garage.domain.model.ParkingEvent
import br.com.codigoalvo.garage.domain.model.Sector
import br.com.codigoalvo.garage.domain.model.Spot
import br.com.codigoalvo.garage.domain.repository.ParkingEventRepository
import br.com.codigoalvo.garage.domain.repository.SpotRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.time.LocalTime
import java.util.*

class WebhookEventServiceTest {

    private lateinit var spotRepository: SpotRepository
    private lateinit var parkingEventRepository: ParkingEventRepository
    private lateinit var objectMapper: ObjectMapper
    private lateinit var webhookEventService: WebhookEventService

    @BeforeEach
    fun setUp() {
        spotRepository = mock(SpotRepository::class.java)
        parkingEventRepository = mock(ParkingEventRepository::class.java)
        objectMapper = ObjectMapper()

        webhookEventService = WebhookEventService(
            spotRepository = spotRepository,
            parkingEventRepository = parkingEventRepository,
            objectMapper = objectMapper
        )
    }

    @Test
    fun `deve salvar evento ENTRY corretamente`() {
        // Arrange
        val payload = """
            {
                "license_plate": "ABC1234",
                "entry_time": "2025-05-20T12:00:00Z",
                "event_type": "ENTRY"
            }
        """.trimIndent()

        val captor = ArgumentCaptor.forClass(ParkingEvent::class.java)

        // Act
        webhookEventService.processRawEvent(payload)

        // Assert
        verify(parkingEventRepository).save(captor.capture())
        val eventoSalvo = captor.value

        assert(eventoSalvo.licensePlate == "ABC1234")
        assert(eventoSalvo.eventType == EventType.ENTRY)
        assert(eventoSalvo.spot == null)
    }
}
