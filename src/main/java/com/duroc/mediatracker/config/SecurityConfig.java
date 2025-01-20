package com.duroc.mediatracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity // (debug = true) if you wish to debug the filter (see what requests are coming in and the details of them)
@EnableMethodSecurity

/**
 * Security Configuration for the users endpoints
 * Will apply the custom filter made to requests trying to reach the users endpoint and stop any unauthenticated requests
 */
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.securityMatcher("/api/v1/mediatracker/users/**")
                .addFilterAfter(new FirebaseBearerTokenFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers(
                                        "/api/v1/mediatracker/users/**").authenticated()
                );

        return http.build();
    }
}
