package com.etiennek.gw.iam.mvc;

import java.net.URL;
import java.util.Locale;

import javax.validation.Valid;

import com.etiennek.gw.EtkProperties;
import com.etiennek.gw.iam.models.MessageToken;
import com.etiennek.gw.iam.repos.MessageTokenRepository;
import com.etiennek.gw.iam.repos.UserRepository;
import com.etiennek.gw.msg.email.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/iam")
public class ResetPasswordController {

    private @Autowired MessageTokenRepository messageTokenRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired EmailService emailService;
    private @Autowired TemplateEngine templateEngine;
    private @Autowired EtkProperties etkProperties;

    @GetMapping("/reset-password")
    String resetPassword(@ModelAttribute ResetPasswordRequest resetPasswordRequest) {
        return "iam/reset-password";
    }

    @PostMapping("/reset-password")
    Mono<String> resetPasswordRequest(@Valid @ModelAttribute ResetPasswordRequest resetPasswordRequest,
            BindingResult result, @RequestHeader(HttpHeaders.USER_AGENT) String userAgentString) {
        String emailLower = resetPasswordRequest.getEmail().toLowerCase();
        return userRepository.findByEmailLower(emailLower).flatMap(user -> {
            if (user == null) {
                return Mono.just("iam/reset-password");
            }

            return messageTokenRepository
                    .save(new MessageToken(user.getId(), userAgentString == null ? "UNKNOWN" : userAgentString))
                    .flatMap(mt -> {
                        Context ctx = new Context(Locale.getDefault());
                        ctx.setVariable("productName", etkProperties.getProductName());
                        ctx.setVariable("username", user.getUsername());
                        ctx.setVariable("supportUrl", etkProperties.getSupportUrl());
                        ctx.setVariable("baseUrl", etkProperties.getBaseUrl());
                        ctx.setVariable("userAgent", mt.getUserAgent());
                        try {
                            ctx.setVariable("resetPasswordUrl",
                                    new URL(etkProperties.getBaseUrl(), "/iam/reset-password/" + mt.getId()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        String body = templateEngine.process("iam/email/reset-password", ctx);
                        return emailService.send(etkProperties.getSupportEmail().toString(),
                                "Set up a new password for " + etkProperties.getProductName(), user.getEmail(),
                                "text/html", body);
                    }).map(emailSendReply -> "iam/reset-password");
        });
    }
}
