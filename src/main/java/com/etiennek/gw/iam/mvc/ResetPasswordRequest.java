package com.etiennek.gw.iam.mvc;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.etiennek.gw.iam.Regexp;

import org.hibernate.validator.constraints.Length;

public class ResetPasswordRequest {

    @NotBlank
    @Length(max = 255)
    @Email
    @Pattern(regexp = Regexp.EMAIL)
    private String email;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

}