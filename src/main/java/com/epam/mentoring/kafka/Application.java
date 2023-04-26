package com.epam.mentoring.kafka;

import java.util.concurrent.ExecutionException;

public class Application {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String topic = "test-topic";
        AtLeastOnceKafkaProducer atLeastOnceKafkaProducer = new AtLeastOnceKafkaProducer(topic);
        atLeastOnceKafkaProducer.sendMessages();
        AtMostOnceKafkaConsumer atMostOnceKafkaConsumer = new AtMostOnceKafkaConsumer(topic);
        atMostOnceKafkaConsumer.processRecords();
    }
}
