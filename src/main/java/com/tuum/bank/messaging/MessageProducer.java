package com.tuum.bank.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public <T extends Serializable> void publishMessage(String msgQueue, String msgPayload) {
        rabbitTemplate.convertAndSend(msgQueue, msgPayload);
    }
}

