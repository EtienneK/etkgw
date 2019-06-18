package com.etiennek.gw.iam.mvc;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.etiennek.gw.iam.Regexp;

import org.hibernate.validator.constraints.Length;

public class RegisterRequest {

    @NotBlank
    @Length(min = 2, max = 255)
    @Pattern(regexp = Regexp.USERNAME)
    private String username;

    @NotBlank
    @Length(max = 255)
    @Email
    @Pattern(regexp = Regexp.EMAIL)
    private String email;

    @NotBlank
    @Length(min = 6, max = 255)
    private String password;

    @NotBlank
    @Length(min = 6, max = 255)
    private String passwordAgain;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordAgain() {
        return this.passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

}