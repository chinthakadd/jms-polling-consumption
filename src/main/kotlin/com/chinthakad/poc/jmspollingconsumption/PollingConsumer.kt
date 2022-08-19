package com.chinthakad.poc.jmspollingconsumption

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.connection.JmsResourceHolder
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.SessionCallback
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.annotation.PostConstruct
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.TextMessage


@Component
class PollingConsumer {

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate
    private val counter: AtomicLong = AtomicLong()

    @PostConstruct
    fun initConsumption() {
        val execSvc = Executors.newScheduledThreadPool(10)
        execSvc.scheduleAtFixedRate({
            execSvc.execute {

                jmsTemplate.execute(SessionCallback {
                    val pollBeforeTimestamp = Instant.now().minusSeconds(10).toEpochMilli().toString()
                    val consumer = it.createConsumer(
                        jmsTemplate.destinationResolver.resolveDestinationName(
                            it, "DEV.QUEUE.1", false
                        ), "JMSTimestamp < $pollBeforeTimestamp"
                    )
                    // Use transaction timeout (if available).
                    val message: Message = consumer.receive(5000)
                    if (message != null)
                        println(
                            "Thread: ${Thread.currentThread().name} - Received message: ${(message as TextMessage).text}" +
                                    " : count - ${counter.incrementAndGet()}"
                        )
                    it.commit()
                }, true)
            }
        }, 0, 50, TimeUnit.MILLISECONDS);

    }
}