/*package com.example.real_estate_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // API REST → niente CSRF
            .csrf(csrf -> csrf.disable())

            // 🔥 IMPORTANTISSIMO: stateless
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔥 disabilita completamente auth mechanisms
            .httpBasic(Customizer.withDefaults())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())

            // tutto permesso (per ora)
            .authorizeHttpRequests(auth ->
                auth.anyRequest().permitAll()
            );

        return http.build();
    }
}
*/