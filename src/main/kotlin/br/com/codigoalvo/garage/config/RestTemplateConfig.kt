package br.com.codigoalvo.garage.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(objectMapper: ObjectMapper): RestTemplate {
        val converter = MappingJackson2HttpMessageConverter(objectMapper)
        return RestTemplate(listOf(converter))
    }
}
