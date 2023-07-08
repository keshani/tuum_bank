package com.tuum.bank.messaging;

import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public  final MessageProducer messageProducer;

    public MessageService(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    public Boolean sendMessage(Message msg) {
        this.messageProducer.publishMessage(msg.getQueueName(), msg.getMessagePayLoad());
        return true;
    }
}
