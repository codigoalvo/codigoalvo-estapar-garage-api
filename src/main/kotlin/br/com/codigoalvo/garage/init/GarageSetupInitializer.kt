package br.com.codigoalvo.garage.init

import br.com.codigoalvo.garage.service.GarageSetupService
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GarageSetupInitializer {

    private val logger = LoggerFactory.getLogger(GarageSetupInitializer::class.java)

    @Bean
    fun runGarageSetup(garageSetupService: GarageSetupService) = ApplicationRunner {
        try {
            logger.info("Executando setup automático da garagem ao iniciar a aplicação...")
            garageSetupService.initializeGarage()
            logger.info("Setup da garagem concluído com sucesso.")
        } catch (e: Exception) {
            logger.warn("Não foi possível realizar o setup automático da garagem à partir do simulador: ${e.message}")
        }
    }
}
