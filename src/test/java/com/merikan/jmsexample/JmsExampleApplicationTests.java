package com.merikan.jmsexample;

import com.merikan.jmsexample.jms.Receiver;
import com.merikan.jmsexample.jms.Sender;
import org.apache.activemq.artemis.junit.EmbeddedActiveMQResource;
import org.apache.activemq.artemis.junit.EmbeddedJMSResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JmsExampleApplicationTests {

    @Rule
    public EmbeddedJMSResource resource = new EmbeddedJMSResource();

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @Test
    public void testReceive() throws Exception {
        sender.send("Hello Artemis!");

        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        assertThat(receiver.getLatch().getCount()).isEqualTo(0);
    }
}