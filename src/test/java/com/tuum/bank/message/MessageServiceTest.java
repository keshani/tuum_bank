//package com.tuum.bank.message;
//
//import com.tuum.bank.messaging.MessageService;
//import org.junit.jupiter.api.Test;
//        import org.springframework.amqp.core.Message;
//        import org.springframework.amqp.rabbit.core.RabbitTemplate;
//        import org.springframework.beans.factory.annotation.Autowired;
//        import org.springframework.boot.test.context.SpringBootTest;
//        import org.springframework.test.context.ActiveProfiles;
//
//@SpringBootTest
//@
//public class MessageServiceTest {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    private MessageService messageService;
//
//    @Test
//    public void test_publish_msg_to_queue() {
//        // Define the exchange and queue names
//        String exchangeName = "myExchange";
//        String queueName = "myQueue";
//
//        // Define the message content
//        String message = "Hello, RabbitMQ!";
//
//        // Publish the message to the exchange
//        rabbitTemplate.convertAndSend(exchangeName, "", message);
//
//        // Consume the message from the queue
//        Message receivedMessage = rabbitTemplate.receive(queueName);
//
//        // Verify that the received message matches the expected content
//        assertNotNull(receivedMessage);
//        assertEquals(message, new String(receivedMessage.getBody()));
//    }
//}
