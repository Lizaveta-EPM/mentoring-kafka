package com.epam.mentoring.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Coordinate {

    private double latitude;
    private double longitude;
}
