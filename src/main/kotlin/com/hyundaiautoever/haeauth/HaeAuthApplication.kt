package com.hyundaiautoever.haeauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication


@ConfigurationPropertiesScan
@SpringBootApplication
class HaeAuthApplication

fun main(args: Array<String>) {
    runApplication<HaeAuthApplication>(*args)
}
