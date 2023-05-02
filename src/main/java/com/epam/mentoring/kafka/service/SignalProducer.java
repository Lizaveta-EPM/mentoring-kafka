package com.epam.mentoring.kafka.service;

import com.epam.mentoring.kafka.domain.VehicleSignal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignalProducer {

    private final KafkaTemplate<String, VehicleSignal> vehicleKafkaTemplate;

    @Value("${kafka.topic.signal-producer}")
    private String input;

    public void send(@NonNull VehicleSignal signal) {
        vehicleKafkaTemplate.send(input, signal);
    }
}
