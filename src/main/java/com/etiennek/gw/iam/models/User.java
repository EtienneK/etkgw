package com.etiennek.gw.iam.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.etiennek.gw.iam.Regexp;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document
public class User implements UserDetails {
    private static final long serialVersionUID = -5080858555110573209L;

    private final @Id String id;

    @NotBlank
    @Length(min = 2, max = 255)
    @Pattern(regexp = Regexp.USERNAME)
    private final String username;

    @Indexed(unique = true)
    private final String usernameLower;

    @NotBlank
    @Length(max = 255)
    @Email
    @Pattern(regexp = Regexp.EMAIL)
    private final String email;

    @Indexed(unique = true)
    private final String emailLower;

    @NotBlank
    private final String password;

    @PersistenceConstructor
    User(String id, String username, String usernameLower, String email, String emailLower, String password) {
        this.id = id;
        this.username = username;
        this.usernameLower = usernameLower;
        this.email = email;
        this.emailLower = emailLower;
        this.password = password;
    }

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.usernameLower = username.toLowerCase();
        this.email = email;
        this.emailLower = email.toLowerCase();
        this.password = password;
    }

    public String getId() {
        return this.id;
    }

    public String getUsernameLower() {
        return this.usernameLower;
    }

    public String getEmail() {
        return this.email;
    }

    public String getEmailLower() {
        return emailLower;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
