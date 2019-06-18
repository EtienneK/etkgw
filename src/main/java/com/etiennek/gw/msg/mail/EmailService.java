package com.etiennek.gw.msg.mail;

import reactor.core.publisher.Mono;

public interface EmailService {
    Mono<Void> send(String from, String subject, String to, String contentType, String body);
}
