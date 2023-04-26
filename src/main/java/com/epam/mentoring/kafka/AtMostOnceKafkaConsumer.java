package com.epam.mentoring.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class AtMostOnceKafkaConsumer {
    private final Consumer<String, String> consumer;

    public AtMostOnceKafkaConsumer(String topic, Consumer<String, String> consumer) {
        this.consumer = consumer;
        consumer.subscribe(Arrays.asList(topic));
    }

    public AtMostOnceKafkaConsumer(String topic) {
        Properties props = getProperties();
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
    }

    public ConsumerRecords<String, String> processRecords() {
        return consumer.poll(Duration.ofMillis(100));
    }

    public void close() {
        consumer.close();
    }

    private Properties getProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:29092");
        String consumeGroup = "cg1";
        props.put("group.id", consumeGroup);

        // Set this property, if auto commit should happen.
        props.put("enable.auto.commit", "true");

        // Auto commit interval, kafka would commit offset at this interval.
        props.put("auto.commit.interval.ms", "101");

        // This is how to control number of records being read in each poll
        props.put("max.partition.fetch.bytes", "135");

        props.put("heartbeat.interval.ms", "3000");
        props.put("session.timeout.ms", "6001");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }
}
