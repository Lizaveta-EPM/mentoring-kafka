package com.epam.mentoring.kafka.controller;

import com.epam.mentoring.kafka.domain.VehicleSignal;
import com.epam.mentoring.kafka.service.SignalProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/signals", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SignalController {

    private final SignalProducer signalProducer;

    @PostMapping
    public void sendSignal(@RequestBody VehicleSignal signal) {
        signalProducer.send(signal);
    }
}
