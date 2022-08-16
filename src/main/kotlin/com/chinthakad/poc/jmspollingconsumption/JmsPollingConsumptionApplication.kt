package com.chinthakad.poc.jmspollingconsumption

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jms.annotation.EnableJms

@SpringBootApplication
@EnableJms
class JmsPollingConsumptionApplication

fun main(args: Array<String>) {
    runApplication<JmsPollingConsumptionApplication>(*args)
}
