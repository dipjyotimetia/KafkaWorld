package com;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Producer {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(Producer.class);

        String bootstrapServer = "127.0.0.1:9092";
        // Create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 200; i++) {
            // Create a producer record
            String topic = "second-topic";
            String value = "hello kafka";
            String key = "Key_" + i;
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            // Send Data
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    // Execute every time record is successfully send
                    if (e == null) {
                        logger.info("Received new metadata. \n" +
                                "Topic: " + metadata.topic() + "\n" +
                                "Partition: " + metadata.partition() + "\n" +
                                "Offset: " + metadata.offset() + "\n" +
                                "Timestamp: " + metadata.timestamp());
                    } else {
                        logger.error("Error while producing" + e.getMessage());
                    }
                }
            });
        }

        producer.flush();
        producer.close();
    }
}
