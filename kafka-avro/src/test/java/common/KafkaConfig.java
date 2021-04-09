package common;

import com.config.AppConfig;
import com.typesafe.config.ConfigFactory;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaConfig {
    private AppConfig appConfig;

    public KafkaConfig() {
        appConfig = new AppConfig(ConfigFactory.load());
    }

    public Properties getKafkaStreamsConfig() {
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
}
