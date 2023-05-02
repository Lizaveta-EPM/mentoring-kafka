package com.epam.mentoring.kafka.service;

import com.epam.mentoring.kafka.domain.DistanceInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistanceConsumer {

    @KafkaListener(topics = "output", groupId = "myGroup")
    public void receive(@NonNull DistanceInfo distanceInfo) {
        log.info("DistanceInfo received: {}", distanceInfo);
    }
}
