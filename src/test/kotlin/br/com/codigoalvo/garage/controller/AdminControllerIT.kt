package br.com.codigoalvo.garage.controller

import br.com.codigoalvo.garage.AbstractPostgresTest
import br.com.codigoalvo.garage.domain.enums.MessageKey
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.util.ResourceUtils
import java.nio.file.Files

@AutoConfigureMockMvc
class AdminControllerIT : AbstractPostgresTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var entityManager: EntityManager

    val requestJson = Files.readString(
        ResourceUtils.getFile("classpath:requests/admin-setup-request.json").toPath()
    )

    @Test
    fun `POST admin-setup should return metadata with 2 sectors and 4 spots`() {
        mockMvc.post("/admin/setup") {
            contentType = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.localization_key") { value(MessageKey.GARAGE_SETUP_SUCCESS.key) }
            jsonPath("$.metadata.sectors") { value(2) }
            jsonPath("$.metadata.spots") { value(4) }
        }

        val sectorCount = entityManager.createQuery("SELECT COUNT(s) FROM Sector s").singleResult as Long
        val spotCount = entityManager.createQuery("SELECT COUNT(s) FROM Spot s").singleResult as Long
        assertEquals(2, sectorCount)
        assertEquals(4, spotCount)
    }
}