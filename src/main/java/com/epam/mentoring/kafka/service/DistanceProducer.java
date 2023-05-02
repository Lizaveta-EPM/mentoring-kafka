package com.epam.mentoring.kafka.service;

import com.epam.mentoring.kafka.domain.Coordinate;
import com.epam.mentoring.kafka.domain.DistanceInfo;
import com.epam.mentoring.kafka.domain.VehicleSignal;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistanceProducer {

    private final KafkaTemplate<String, DistanceInfo> distanceKafkaTemplate;
    private final ConcurrentHashMap<String, Coordinate> latestCoordinates = new ConcurrentHashMap<>();

    @Value("${kafka.topic.distance-producer}")
    private String output;

    @KafkaListener(topics = "input", groupId = "myGroup")
    public void receiveAndSend(@NonNull VehicleSignal signal) {
        DistanceInfo distanceInfo = calculateDistance(signal);
        distanceKafkaTemplate.send(output, distanceInfo);
        log.info("DistanceInfo {} was sent ", distanceInfo);
    }

    private DistanceInfo calculateDistance(VehicleSignal signal) {
        Coordinate coordinate = latestCoordinates.compute(signal.getVehicleId(), (id, oldCoordinates) -> {
            if (oldCoordinates != null) {
                return new Coordinate(signal.getCoordinate().getLatitude() - oldCoordinates.getLatitude(),
                    signal.getCoordinate().getLongitude() - oldCoordinates.getLongitude());
            }
            return signal.getCoordinate();
        });
        double distance = Math.sqrt(Math.pow(coordinate.getLatitude(), 2) + Math.pow(coordinate.getLongitude(), 2));
        return new DistanceInfo(signal.getVehicleId(), distance);
    }
}
