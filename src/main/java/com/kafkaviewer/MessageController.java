package com.kafkaviewer;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/msg")
public class MessageController {

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "OK";
    }
}
