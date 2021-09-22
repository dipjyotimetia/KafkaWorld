package com.dip.spring.controller;

import com.avro.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public final class KafkaController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaController.class);
    private static final String TOPIC = "test-topic";

    @Autowired
    private KafkaTemplate<String, Customer> kafkaTemplate;

    @PostMapping(value = "/producer", consumes = "application/json")
    public String postModelToKafka(@RequestBody Customer emp) {
        kafkaTemplate.send(TOPIC, emp);
        return "Published successfully";
    }
}
