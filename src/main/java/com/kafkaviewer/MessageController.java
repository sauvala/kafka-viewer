package com.kafkaviewer;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

import javax.inject.Inject;

@Controller("/msg")
public class MessageController {
    @Inject
    MessageClient messageClient;

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        messageClient.sendMessage("Watch");
        return "Message delivered";
    }
}
