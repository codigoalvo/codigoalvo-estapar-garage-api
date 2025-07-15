package br.com.codigoalvo.garage.domain.repository

import br.com.codigoalvo.garage.AbstractPostgresTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal

class SectorRepositoryIT : AbstractPostgresTest() {

    @Autowired
    lateinit var sectorRepository: SectorRepository

    @Test
    @Sql("/scripts/sector-setup.sql")
    fun `findByCode should return sector when code exists`() {
        val foundSector = sectorRepository.findByCode("A")
        assertNotNull(foundSector)
        assertEquals("A", foundSector?.code)
        assertEquals(BigDecimal("10.00"), foundSector?.basePrice)
        assertEquals(50, foundSector?.capacity)
    }

    @Test
    fun `findByCode should return null when code not exists`() {
        val foundSector = sectorRepository.findByCode("INVALID")
        assertNull(foundSector)
    }

    @Test
    @Sql("/scripts/sector-setup.sql")
    fun `debug - show database state`() {
        val allSectors = sectorRepository.findAll()
        println("Sectors in database: ${allSectors.size}")
        allSectors.forEach { println(it) }
    }

}