package com.epam.mentoring.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class AtLeastOnceKafkaProducer {

    private static final int NUM_OF_MESSAGES = 5;
    private final String topic;
    private final Producer<String, String> producer;

    public AtLeastOnceKafkaProducer(String topic, Producer<String, String> producer) {
        this.topic = topic;
        this.producer = producer;
    }

    public AtLeastOnceKafkaProducer(String topic) {
        this.topic = topic;
        Properties props = getProducerProperties();
        this.producer = new KafkaProducer<>(props);
    }

    public void sendMessages() throws ExecutionException, InterruptedException {
        sendMessages(1, 1);
        sendMessages(2, 2);
    }

    public void close() {
        producer.close();
    }

    private void sendMessages(int partition, long record) {
        for (int i = 0; i < NUM_OF_MESSAGES; i++) {
            producer.send(new ProducerRecord<>(topic, partition, Long.toString(record), Long.toString(record++)));
        }
    }

    private Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:29092");
        props.put("acks", "all");
        props.put("retries", 1);
        props.put("batch.size", NUM_OF_MESSAGES);
        props.put("linger.ms", 1);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }
}
