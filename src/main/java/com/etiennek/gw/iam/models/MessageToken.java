package com.etiennek.gw.iam.models;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MessageToken {
    private final @Id String id;
    @NotBlank
    private final String userId;
    private final String userAgent;

    public MessageToken(String userId, String userAgent) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.userAgent = userAgent;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

}
