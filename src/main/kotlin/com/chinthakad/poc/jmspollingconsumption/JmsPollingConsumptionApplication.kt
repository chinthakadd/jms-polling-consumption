package com.chinthakad.poc.jmspollingconsumption

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jms.annotation.EnableJms
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
class JmsPollingConsumptionApplication

fun main(args: Array<String>) {
    runApplication<JmsPollingConsumptionApplication>(*args)
}
