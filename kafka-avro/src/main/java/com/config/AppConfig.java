package com.config;

import com.typesafe.config.Config;
import lombok.Getter;

@Getter
public class AppConfig {

    private final String bootstrapServers;
    private final String schemaRegistryUrl;
    private final String validTopicName;

    public AppConfig(Config config) {
        this.bootstrapServers = config.getString("kafka.bootstrap.servers");
        this.schemaRegistryUrl = config.getString("kafka.schema.registry.url");
        this.validTopicName = config.getString("kafka.valid.topic.name");
    }
}
