package com.merikan.jmsexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class JmsExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmsExampleApplication.class, args);
    }

}
