package com.etiennek.gw.iam.repos;

import com.etiennek.gw.iam.models.User;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<Boolean> existsByUsernameLower(String usernameLower);

    Mono<Boolean> existsByEmailLower(String emailLower);

    Mono<User> findByUsernameLower(String usernameLower);

    Mono<User> findByEmailLower(String emailLower);
}
