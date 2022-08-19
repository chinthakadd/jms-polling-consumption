package com.chinthakad.poc.jmspollingconsumption

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.connection.JmsResourceHolder
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.SessionCallback
import org.springframework.jms.support.JmsUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.lang.Exception
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.annotation.PostConstruct
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.MessageConsumer
import javax.jms.TextMessage


@Component
class PollingConsumer {

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate
    private val counter: AtomicLong = AtomicLong()
    private var beforeTimestamp = Instant.now().minusSeconds(10)

    @PostConstruct
    fun initConsumption() {
        val execSvc = Executors.newScheduledThreadPool(50)
        execSvc.scheduleAtFixedRate({
            execSvc.execute {
                jmsTemplate.execute(SessionCallback {
                    var consumer: MessageConsumer? = null
                    try {
                        consumer = it.createConsumer(
                            jmsTemplate.destinationResolver.resolveDestinationName(
                                it, "DEV.QUEUE.1", false
                            ),
                            "JMSTimestamp < ${beforeTimestamp.minusSeconds(30).toEpochMilli()}"
                        )
                        // Use transaction timeout (if available).
                        val message: Message? = consumer.receive(5000)
                        if (message != null) {
                            println(
                                """[Thread: ${Thread.currentThread().name} 
                                    || Received message: ${(message as TextMessage).text}  
                                    ||  count - ${counter.incrementAndGet()}]"""
                                    .trimMargin()
                            )
                            val text = (message as TextMessage).text
                            if ("bad".equals(text)) {
                                println("Bad Message Received")
                                it.rollback()
                            } else {
                                it.commit()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        updateTimestamp()
                        JmsUtils.closeMessageConsumer(consumer);
                    }
                }, true)
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
    }

    fun updateTimestamp() {
        if (Instant.now().toEpochMilli() - beforeTimestamp.toEpochMilli() <= 30_000) return
        synchronized(this) {
            if (Instant.now().toEpochMilli() - beforeTimestamp.toEpochMilli() > 30_000) {
                println("Change Message Selector")
                beforeTimestamp = Instant.now()
            }
        }
    }
}