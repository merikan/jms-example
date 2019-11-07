package com.merikan.jmsexample.jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.XAConnectionFactory;

@Configuration
public class JmsConfig {

    @Value("${artemis.broker-url}")
    private String brokerUrl;

    @Bean
    public ConnectionFactory receiverConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setTargetConnectionFactory(new ActiveMQConnectionFactory(brokerUrl));
        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory receiverConnectionFactory) {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        ConnectionFactory connectionFactory = receiverConnectionFactory;
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("3-10");
        return factory;
    }

    @Bean
    public Receiver receiver() {
        return new Receiver();
    }


    @Bean
    public ConnectionFactory senderConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setTargetConnectionFactory(new ActiveMQConnectionFactory(brokerUrl));
        return connectionFactory;
    }


    @Bean
    public JmsTemplate senderJmsTemplate(ConnectionFactory senderConnectionFactory) {
        return new JmsTemplate(senderConnectionFactory);
    }

    @Bean
    public Sender sender() {
        return new Sender();
    }

}
