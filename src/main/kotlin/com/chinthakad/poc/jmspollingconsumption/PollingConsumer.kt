package com.chinthakad.poc.jmspollingconsumption

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import java.time.Instant
import javax.annotation.PostConstruct
import javax.jms.TextMessage

@Component
class PollingConsumer {

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @PostConstruct
    fun initConsumption() {
        // TODO: Use a Thread Pool Executor
        // TODO: Connection Pool and other settings
        // TODO: Any Transaction related stuff?
        Thread {
            while (true) {
                val pollBeforeTimestamp = Instant.now().minusSeconds(120).toEpochMilli().toString()
                val message = jmsTemplate.receiveSelected(
                    "DEV.QUEUE.1",
                    "JMSTimestamp < $pollBeforeTimestamp"
                )
                if (message != null) println("Received message: ${(message as TextMessage).text}")
                Thread.sleep(1000)
            }
        }.start()
    }
}