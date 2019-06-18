package com.etiennek.gw.iam.models;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MessageToken {
    private @Id String id;
    private final String userId;

    public MessageToken(String userId) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }
}
