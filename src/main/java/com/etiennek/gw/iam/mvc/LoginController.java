package com.etiennek.gw.iam.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/iam")
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "iam/login";
    }
}
