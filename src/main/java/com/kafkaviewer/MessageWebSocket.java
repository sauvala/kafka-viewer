package com.kafkaviewer;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subscribers.DisposableSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@ServerWebSocket("/ws/listen/{topic}")
public class MessageWebSocket {
    private final WebSocketBroadcaster broadcaster;
    private final KafkaListener kafkaListener;
    private final Map<String,Disposable> messageSubscription;
    private final DisposableSubscriber<String> subscriber;
    private static final Logger LOG = LoggerFactory.getLogger(MessageWebSocket.class);


    public MessageWebSocket(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
        this.kafkaListener = new KafkaListener();
        this.messageSubscription = new HashMap<>();
        this.subscriber = new DisposableSubscriber<>() {

            @Override
            public void onNext(String s) {
                sendMessage(s);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                LOG.info("onComplete()!");
            }
        };
        System.out.println("MessageWebsocket constructor.");
    }

    @OnOpen
    public void onOpen(String topic, WebSocketSession session) {
        if (!messageSubscription.containsKey(topic)) {
            kafkaListener
                    .Listen(topic)
                    .subscribeWith(this.subscriber);
            LOG.info("New subscription created");
        }
        LOG.info("Using existing subscription.");
    }

    private void sendMessage(String message) {
        LOG.info("Sending message");
        broadcaster.broadcastSync(message);
        if (this.subscriber != null) {
            this.subscriber.dispose();
            LOG.info("Disposing subscription");
        }
    }

    @OnMessage
    public void onMessage(
            String topic,
            String message,
            WebSocketSession session) {
        broadcaster.broadcastSync(topic);
        LOG.info("onMessage");
    }

    @OnClose
    public void onClose(
            String topic,
            WebSocketSession session) {
        if (session.getOpenSessions().size() == 0) {
            Disposable subscription = messageSubscription.remove(topic);
            subscription.dispose();
            LOG.info("Subscription closed");
        }
    }
}
