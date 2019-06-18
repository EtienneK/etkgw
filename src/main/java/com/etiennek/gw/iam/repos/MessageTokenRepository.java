package com.etiennek.gw.iam.repos;

import com.etiennek.gw.iam.models.MessageToken;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MessageTokenRepository extends ReactiveCrudRepository<MessageToken, String> {

}