package com.kafkaviewer;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaListener {
    private final Map<String,Flowable<String>> listeners;
    private final Properties props;
    private static final Logger LOG = LoggerFactory.getLogger(MessageWebSocket.class);

    public KafkaListener() {
        this.listeners = new HashMap<>();
        props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "kafka-viewer");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    }

    public Flowable<String> Listen(String topic) {
        if (listeners.containsKey(topic)) {
            return listeners.get(topic);
        } else {
            Flowable<String> source = Flowable.create(emitter -> {
                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Collections.singletonList(topic));
                boolean keepPolling = true;
                while (keepPolling) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        if (emitter.isCancelled()) {
                            keepPolling = false;
                        }
                        LOG.info(String.format("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value()));
                        emitter.onNext(record.value());
                    }
                }
            }, BackpressureStrategy.LATEST);
            listeners.put(topic, source);
            return source;
        }
    }
}
