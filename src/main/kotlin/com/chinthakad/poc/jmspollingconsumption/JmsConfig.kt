package com.chinthakad.poc.jmspollingconsumption

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.connection.CachingConnectionFactory
import org.springframework.jms.core.JmsTemplate
import javax.jms.ConnectionFactory

@Configuration
@EnableJms
class JmsConfig {

    @Bean
    @Primary
    fun jmsTemplate(ibmConnFactory: ConnectionFactory): JmsTemplate {
        val jmsTemplate = JmsTemplate(CachingConnectionFactory(ibmConnFactory))
        jmsTemplate.isSessionTransacted = true
        jmsTemplate.sessionAcknowledgeMode = 2
        return jmsTemplate
    }
}