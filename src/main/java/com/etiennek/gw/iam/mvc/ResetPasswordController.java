package com.etiennek.gw.iam.mvc;

import java.io.IOException;

import javax.validation.Valid;

import com.etiennek.gw.iam.models.MessageToken;
import com.etiennek.gw.iam.repos.MessageTokenRepository;
import com.etiennek.gw.iam.repos.UserRepository;
import com.etiennek.gw.msg.mail.EmailService;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/iam")
public class ResetPasswordController {

    private @Autowired MessageTokenRepository messageTokenRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired EmailService emailService;

    @GetMapping("/reset-password")
    String resetPassword(@ModelAttribute ResetPasswordRequest resetPasswordRequest) {
        return "iam/reset-password";
    }

    @PostMapping("/reset-password")
    Mono<String> resetPasswordRequest(@Valid @ModelAttribute ResetPasswordRequest resetPasswordRequest,
            BindingResult result) {
        String emailLower = resetPasswordRequest.getEmail().toLowerCase();
        return userRepository.findByEmailLower(emailLower).flatMap(user -> {
            if (user == null) {
                return Mono.just("iam/reset-password");
            }
            return messageTokenRepository.save(new MessageToken(user.getId()))
                    .flatMap(
                            mt -> emailService
                                    .send("test@example.com", "Password Reset", user.getEmail(), "text/plain",
                                            "Password reset token: " + mt.getId())
                                    .map(emailSendReply -> "iam/reset-password"));
        });
    }
}
