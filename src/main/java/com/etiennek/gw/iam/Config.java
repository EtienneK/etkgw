package com.etiennek.gw.iam;

import com.etiennek.gw.iam.repos.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

@Configuration
@EnableWebFluxSecurity
public class Config {
    private @Autowired UserRepository userRepository;

    @Bean
    ReactiveUserDetailsService userDetailsService() {
        return (usernameOrEmail) -> {
            String usernameOrEmailLower = usernameOrEmail.toLowerCase().trim();
            if (usernameOrEmailLower.contains("@")) {
                return userRepository.findByEmailLower(usernameOrEmailLower).map(u -> u);
            }
            return userRepository.findByUsernameLower(usernameOrEmailLower).map(u -> u);
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        //@formatter:off
        http
            .csrf()
                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
            .and()
			.authorizeExchange()
				.pathMatchers(
                        "/iam/login",
                        "/iam/register",
                        "/iam/register-confirm",
                        "/iam/reset-password/**",
                        "/gw-assets/**"
                    ).permitAll()
				.anyExchange().authenticated()
				.and()
			.formLogin()
                .loginPage("/iam/login")
                .and()
            .logout()
                .logoutUrl("/iam/logout");
		//@formatter:on
        return http.build();
    }
}
