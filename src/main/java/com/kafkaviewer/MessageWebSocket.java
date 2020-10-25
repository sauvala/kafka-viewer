package com.kafkaviewer;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

@ServerWebSocket("/ws/listen/{topic}")
public class MessageWebSocket {
    private final WebSocketBroadcaster broadcaster;
    private final KafkaMessageListener kafkaMessageListener;

    public MessageWebSocket(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
        this.kafkaMessageListener = new KafkaMessageListener();
    }

    @OnOpen
    public void onOpen(String topic, WebSocketSession session) {
        kafkaMessageListener
                .Listen(topic)
                .subscribe(broadcaster::broadcastSync, Throwable::printStackTrace);
    }

    @OnMessage
    public void onMessage(
            String topic,
            String message,
            WebSocketSession session) {
        broadcaster.broadcastSync(topic);
    }

    @OnClose
    public void onClose(
            String topic,
            WebSocketSession session) {
    }
}
