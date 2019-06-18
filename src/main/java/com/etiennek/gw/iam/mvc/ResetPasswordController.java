package com.etiennek.gw.iam.mvc;

import java.util.Locale;

import javax.validation.Valid;

import com.etiennek.gw.EtkProperties;
import com.etiennek.gw.iam.models.MessageToken;
import com.etiennek.gw.iam.repos.MessageTokenRepository;
import com.etiennek.gw.iam.repos.UserRepository;
import com.etiennek.gw.msg.email.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    //@GetMapping("/reset-password")
    String resetPassword(@ModelAttribute ResetPasswordRequest resetPasswordRequest) {
        return "iam/reset-password";
    }

    @ResponseBody
    @GetMapping("/reset-password")
    ResponseEntity<String> templateTest() {
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("productName", etkProperties.getProductName());

        String body = templateEngine.process("iam/email/reset-password", ctx);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(body);
    }

    @PostMapping("/reset-password")
    Mono<String> resetPasswordRequest(@Valid @ModelAttribute ResetPasswordRequest resetPasswordRequest,
            BindingResult result) {
        String emailLower = resetPasswordRequest.getEmail().toLowerCase();
        return userRepository.findByEmailLower(emailLower).flatMap(user -> {
            if (user == null) {
                return Mono.just("iam/reset-password");
            }
            return messageTokenRepository.save(new MessageToken(user.getId())).flatMap(mt -> {
                String body = templateEngine.process("iam/email/layout", new Context(Locale.getDefault()));
                return emailService.send("test@example.com", "Password Reset", user.getEmail(), "text/html", body);
            }).map(emailSendReply -> "iam/reset-password");
        });
    }
}
