package com.comfandi.phobos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class HttpSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/courses/users").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/v1/courses/users/**").authenticated()
                        .requestMatchers("/api/v1/workshops/users/**").authenticated()
                        .requestMatchers("/api/v1/billing/**").authenticated()
                        .requestMatchers("/api/v1/amortization/**").authenticated()
                        .requestMatchers("/api/v1/unavailable-users/**").authenticated()
                        .requestMatchers("/api/v1/fomento-docs/**").authenticated()
                        .requestMatchers("/api/v1/validacion/**").authenticated()
                        .requestMatchers("/api/v1/rap/**").authenticated()
                        .requestMatchers("/api/v1/consultas/**").authenticated()
                        .requestMatchers("/api/v1/documentos/**").authenticated()
                        .requestMatchers("/api/v1/fosfec-status/**").authenticated()
                        .requestMatchers("/api/v1/subsanacion/**").authenticated()
                )
                .csrf(csrf -> csrf.disable()).cors(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults()) // aún válida y clara
                .build();
    }
}
