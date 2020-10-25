package com.kafkaviewer;

import io.micronaut.configuration.kafka.annotation.*;

@KafkaClient
public interface MessageClient {
    @Topic("test.foo.bar")
    void sendMessage(String name);

    void sendMessage(@Topic String topic, @KafkaKey String brand, String name);
}
