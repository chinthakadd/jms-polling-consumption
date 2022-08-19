@file:Suppress("SpringJavaInjectionPointsAutowiringInspection")

package com.chinthakad.poc.jmspollingconsumption

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import javax.jms.TextMessage


@RestController
class MessageController {

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @GetMapping("/send-good-messages")
    fun sendGoodMessages() {
        Thread {
            for(i in 1..1000){
                jmsTemplate.send("DEV.QUEUE.1", MessageCreator {
                    Thread.sleep(10)
                    return@MessageCreator it.createTextMessage("good")
                })
            }
        }.start()
    }

    @GetMapping("/send-bad-messages")
    fun sendBadMessages() {
        Thread {
            for(i in 1..100){
                jmsTemplate.send("DEV.QUEUE.1", MessageCreator {
                    Thread.sleep(10)
                    return@MessageCreator it.createTextMessage("bad")
                })
            }
        }.start()
    }
    /**
     * Refer: https://www.ibm.com/docs/en/ibm-mq/9.1?topic=messages-message-selectors-in-jms
     */
    @GetMapping("/receive")
    fun receiveMessageAfter1Min(): String {
        val pollBeforeTimestamp = Instant.now().minusSeconds(120).toEpochMilli().toString()
        val message = jmsTemplate.receiveSelected(
            "DEV.QUEUE.1",
            "JMSTimestamp < $pollBeforeTimestamp"
        )
        return (message as TextMessage).text
    }
}