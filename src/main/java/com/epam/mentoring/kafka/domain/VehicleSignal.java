package com.epam.mentoring.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleSignal {

    private String vehicleId;
    private Coordinate coordinate;
}
