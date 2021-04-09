import common.KafkaCore;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Properties;

@Testcontainers
public class TC001_TestKafkaProducer {

    @ClassRule
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    final String topic = "second-topic";
    final String value = "hello kafka";

    Properties properties = new Properties();
    KafkaCore core = new KafkaCore();

    @BeforeEach
    public void beforeTest() {
        kafkaContainer.start();
        System.out.println("Starting kafka container");
    }

    @Test
    public void TestKafkaProducer() {
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Create the producer
        core.CreateTopic(topic, value, properties);
    }

    @AfterEach
    public void StopContainer() {
        kafkaContainer.stop();
        System.out.println("Stopping kafka container");
    }
}