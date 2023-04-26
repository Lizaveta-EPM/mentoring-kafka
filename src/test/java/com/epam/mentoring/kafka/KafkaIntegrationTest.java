package com.epam.mentoring.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.DockerImageName;

public class KafkaIntegrationTest {

    private static KafkaContainer kafkaContainer;
    private static String bootstrapServers;

    @BeforeAll
    public static void setUp() {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));
        kafkaContainer.start();
        bootstrapServers = kafkaContainer.getBootstrapServers();
    }

    @AfterAll
    public static void tearDown() {
        kafkaContainer.stop();
    }

    @Test
    void testKafka() throws ExecutionException, InterruptedException, TimeoutException {
        String topicName = "test-topic";
        AdminClient adminClient = AdminClient.create(
            ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        );

        Producer<String, String> producer = new KafkaProducer<>(
            ImmutableMap.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers,
                ProducerConfig.ACKS_CONFIG,
                "all",
                ProducerConfig.RETRIES_CONFIG,
                "1",
                ProducerConfig.CLIENT_ID_CONFIG,
                UUID.randomUUID().toString()
            ),
            new StringSerializer(),
            new StringSerializer()
        );

        adminClient.createTopics(
            ImmutableList.of(
                new NewTopic(topicName, 2, (short) 1)
            )
        ).all().get(30, TimeUnit.SECONDS);

        producer.send(new ProducerRecord<>(topicName, "testcontainers", "rulezzz")).get();

        Consumer<String, String> consumer = new KafkaConsumer<>(
            ImmutableMap.of(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                "true",
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG,
                "tc-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
            ),
            new StringDeserializer(),
            new StringDeserializer()
        );

        consumer.subscribe(ImmutableList.of(topicName));
        Unreliables.retryUntilTrue(
            1,
            TimeUnit.SECONDS,
            () -> {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    return false;
                }

                assertThat(records)
                    .hasSize(1)
                    .extracting(ConsumerRecord::topic, ConsumerRecord::key, ConsumerRecord::value)
                    .containsExactly(tuple(topicName, "testcontainers", "rulezzz"));

                return true;
            }
        );

        consumer.unsubscribe();
    }
}
