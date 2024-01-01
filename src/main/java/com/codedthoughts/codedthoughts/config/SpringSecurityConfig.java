package com.codedthoughts.codedthoughts.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authenticationProvider;

    private final CorsFilter corsFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF
            .csrf(AbstractHttpConfigurer::disable)

            // Set session management to stateless
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Set unauthorized requests exception handler
            .exceptionHandling((exceptionHandler -> {
                exceptionHandler.authenticationEntryPoint(
                        ((request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        })
                );
            }))

            // Set permissions on endpoints
            .authorizeHttpRequests((requestCustomizer) -> requestCustomizer
                    // Our public endpoints
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("api/blog/get-att/**").permitAll()
                    // Our private endpoints
                    .anyRequest().authenticated()
            )

            .authenticationProvider(authenticationProvider)

            // Add CorsFilter
            .addFilterBefore(
                    corsFilter,
                    UsernamePasswordAuthenticationFilter.class
            )

            // Add JWT token filter
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
