package com;

import com.avro.Customer;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.Encoding;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

public class AvroPubSub {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Config config = new Config();
        String projectId = "local-pubsub";
        String topicId = "my-topic-10";

        createTopicExample(projectId, topicId, config);
        publishAvroRecordsExample(projectId, topicId, config);
    }

    public static void createTopicExample(String projectId, String topicId, Config config) throws IOException {
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(TopicAdminSettings.newBuilder()
                .setTransportChannelProvider(config.ChannelProvider())
                .setCredentialsProvider(config.CredentialProvider())
                .build())) {
            TopicName topicName = TopicName.of(projectId, topicId);
            Topic topic = topicAdminClient.createTopic(topicName);
            System.out.println("Created topic: " + topic.getName());
        }
    }

    public static void publishAvroRecordsExample(String projectId, String topicId, Config config)
            throws IOException, ExecutionException, InterruptedException {

        Encoding encoding = null;

        TopicName topicName = TopicName.of(projectId, topicId);

        // Get the topic encoding type.
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
            encoding = topicAdminClient.getTopic(topicName).getSchemaSettings().getEncoding();
        }

        // Instantiate an avro-tools-generated class defined in `us-states.avsc`.
        Customer customer = Customer.newBuilder()
                .setFirstName("John")
                .setLastName("Doe")
                .setAge(25)
                .setHeight(185.5f)
                .setWeight(85.6f)
                .setAutomatedEmail(false)
                .build();

        Publisher publisher = null;

        block:
        try {
            publisher = Publisher.newBuilder(topicName)
                    .setChannelProvider(config.ChannelProvider())
                    .setCredentialsProvider(config.CredentialProvider())
                    .build();

            // Prepare to serialize the object to the output stream.
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            Encoder encoder = null;

            // Prepare an appropriate encoder for publishing to the topic.
            switch (encoding) {
                case BINARY:
                    System.out.println("Preparing a BINARY encoder...");
                    encoder = EncoderFactory.get().directBinaryEncoder(byteStream, /*reuse=*/ null);
                    break;

                case JSON:
                    System.out.println("Preparing a JSON encoder...");
                    encoder = EncoderFactory.get().jsonEncoder(Customer.getClassSchema(), byteStream);
                    break;

                default:
                    break block;
            }

            // Encode the object and write it to the output stream.
            customer.customEncode(encoder);
            encoder.flush();

            // Publish the encoded object as a Pub/Sub message.
            ByteString data = ByteString.copyFrom(byteStream.toByteArray());
            PubsubMessage message = PubsubMessage.newBuilder().setData(data).build();
            System.out.println("Publishing message: " + message);

            ApiFuture<String> future = publisher.publish(message);
            System.out.println("Published message ID: " + future.get());

        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }
}
