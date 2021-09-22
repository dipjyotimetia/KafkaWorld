package com.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ConsumerWIthThreadDemo {
    public static void main(String[] args) {

        new ConsumerWIthThreadDemo().run();

    }

    private ConsumerWIthThreadDemo() {

    }

    private void run() {

        String bootstrapServer = "127.0.0.1:9092";
        String groupId = "my-sixth-application";
        String topic = "second-topic";

        // latch for dealing with multiple thread
        CountDownLatch latch = new CountDownLatch(1);

        log.info("Creating the consumer thread");
        ConsumerThread myConsumerThread = new ConsumerThread(
                latch, bootstrapServer, groupId, topic
        );

        // Start the thread
        Thread myThread = new Thread(myConsumerThread);
        myThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Caught shutdown hook");
            myConsumerThread.shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("Application has exited");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Application got interrupted" + e);
        } finally {
            log.info("Application is closing");
        }

    }

    public class ConsumerThread implements Runnable {
        Logger logger = LoggerFactory.getLogger(ConsumerThread.class.getName());
        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;

        public ConsumerThread(CountDownLatch latch, String bootstrapServer, String groupId, String topic) {
            this.latch = latch;
            // Create consumer config
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // "earliest/latest/none"
            consumer = new KafkaConsumer<String, String>(properties);
            consumer.subscribe(Collections.singletonList(topic));
        }

        @Override
        public void run() {
            // poll for the new data
            try {
                while (true) {
                    ConsumerRecords<String, String> records =
                            consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> record : records) {
                        logger.info("Key: " + record.key() + ", Value: " + record.value());
                        logger.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                    }
                }
            } catch (WakeupException e) {
                logger.info("Received shutdown signal");
            } finally {
                consumer.close();
                // Tell main code we are down with consumer
                latch.countDown();
            }
        }

        public void shutdown() {
            consumer.wakeup();
        }
    }
}
