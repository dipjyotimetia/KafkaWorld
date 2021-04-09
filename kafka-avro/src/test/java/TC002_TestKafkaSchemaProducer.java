import com.avro.Customer;
import common.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.util.Properties;

public class TC002_TestKafkaSchemaProducer {

    final String topic = "second-topic";
    KafkaConfig config = new KafkaConfig();

    @Test
    public void TestKafkaSchemaAvro() {
        Properties configProperty = config.getKafkaStreamsConfig();
        KafkaProducer<String, Customer> kafkaProducer = new KafkaProducer<>(configProperty);
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
        kafkaProducer.send(producerRecord, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Success");
                System.out.println(metadata.toString());
            } else {
                exception.printStackTrace();
            }
        });
    }
}
