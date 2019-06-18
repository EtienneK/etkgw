package com.etiennek.gw;

import java.net.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("etk")
@Validated
public class EtkProperties {
    @NotBlank
    private String productName;

    @NotNull
    private URL supportUrl;

    @Email
    @NotNull
    private String supportEmail;

    @NotNull
    private URL baseUrl;

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public URL getSupportUrl() {
        return this.supportUrl;
    }

    public void setSupportUrl(URL supportUrl) {
        this.supportUrl = supportUrl;
    }

    public URL getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSupportEmail() {
        return this.supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

}
