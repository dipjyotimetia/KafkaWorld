package com.dip.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class KafkaSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaSpringApplication.class, args);
    }

}
