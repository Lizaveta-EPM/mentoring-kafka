package com.epam.mentoring.kafka.config;

import com.epam.mentoring.kafka.domain.DistanceInfo;
import com.epam.mentoring.kafka.domain.VehicleSignal;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;
    @Value("${kafka.topic.signal-producer}")
    private String input;
    @Value("${kafka.topic.distance-producer}")
    private String output;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic input() {
        return TopicBuilder.name(input)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic output() {
        return TopicBuilder.name(output)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public KafkaTemplate<String, VehicleSignal> vehicleKafkaTemplate(ProducerFactory<String, VehicleSignal> producerFactory,
                                                                     ConcurrentKafkaListenerContainerFactory<String, VehicleSignal> listenerFactory) {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);
        listenerFactory.getContainerProperties().setMissingTopicsFatal(false);
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, DistanceInfo> distanceKafkaTemplate(ProducerFactory<String, DistanceInfo> producerFactory,
                                                                     ConcurrentKafkaListenerContainerFactory<String, DistanceInfo> listenerFactory) {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);
        listenerFactory.getContainerProperties().setMissingTopicsFatal(false);
        return kafkaTemplate;
    }
}