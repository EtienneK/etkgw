package com.etiennek.gw.iam.mvc;

import javax.validation.Valid;

import com.etiennek.gw.iam.models.User;
import com.etiennek.gw.iam.mvc.RegisterRequest;
import com.etiennek.gw.iam.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/iam")
public class RegisterController {
    private @Autowired UserRepository userRepository;
    private @Autowired PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    String register(@ModelAttribute RegisterRequest registerRequest) {
        return "iam/register";
    }

    @PostMapping("/register")
    Mono<String> registerRequest(@Valid @ModelAttribute RegisterRequest registerRequest, BindingResult result) {
        if (!registerRequest.getPassword().equals(registerRequest.getPasswordAgain())) {
            result.addError(new FieldError("registerRequest", "passwordAgain", "Passwords didn't match."));
        }

        return userRepository.existsByUsernameLower(registerRequest.getUsername().toLowerCase())
                .flatMap(usernameExists -> {
                    if (usernameExists) {
                        result.addError(new FieldError("registerRequest", "username", "Username already exists."));
                    }
                    return userRepository.existsByEmailLower(registerRequest.getEmail().toLowerCase());
                }).flatMap(emailExists -> {
                    if (emailExists) {
                        result.addError(new FieldError("registerRequest", "email", "Email address already exists."));
                    }

                    if (result.hasErrors()) {
                        return Mono.just("iam/register");
                    }

                    String passwordHashed = passwordEncoder.encode(registerRequest.getPassword());
                    return userRepository
                            .save(new User(registerRequest.getUsername(), registerRequest.getEmail(), passwordHashed))
                            .flatMap(user -> {
                                return Mono.just("redirect:/iam/register-confirm");
                            });
                });
    }

    @GetMapping(value = "/register-confirm")
    public String registerConfirm() {
        return "iam/register-confirm";
    }

}
