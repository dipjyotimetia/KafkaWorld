package com;

import com.avro.Customer;
import com.config.AppConfig;
import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@Slf4j
public class AvroProducer {
    private AppConfig appConfig;

    public static void main(String[] args) throws Exception {
        AvroProducer producer = new AvroProducer();
        producer.start();
    }

    private AvroProducer() {
        appConfig = new AppConfig(ConfigFactory.load());
    }

    private Properties getKafkaStreamsConfig() {
        // Create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "1");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "10");

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapServers());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", appConfig.getSchemaRegistryUrl());
        return properties;
    }

    private void start() throws Exception {
        Properties configProperty = getKafkaStreamsConfig();
        KafkaProducer<String, Customer> kafkaProducer = new KafkaProducer<>(configProperty);
        String topic = appConfig.getValidTopicName();

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            Customer customer = Customer.newBuilder()
                    .setFirstName("John")
                    .setLastName("Doe")
                    .setAge(25)
                    .setHeight(185.5f)
                    .setWeight(85.6f)
                    .setAutomatedEmail(false)
                    .build();

            ProducerRecord<String, Customer> producerRecord = new ProducerRecord<>(
                    topic, customer
            );

            kafkaProducer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        System.out.println("Success");
                        System.out.println(metadata.toString());
                    } else {
                        exception.printStackTrace();
                    }
                }
            });
        }
        kafkaProducer.flush();
        kafkaProducer.close();
    }
}
