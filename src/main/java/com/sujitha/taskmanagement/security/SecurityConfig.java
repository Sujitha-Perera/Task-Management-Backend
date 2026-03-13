package com.sujitha.taskmanagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sujitha.taskmanagement.security.JwtAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                        .cors(withDefaults())
                        .csrf(AbstractHttpConfigurer::disable)

                        .authorizeHttpRequests(auth -> auth

                                // Allow preflight requests (important for CORS)
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                // Public authentication APIs
                                .requestMatchers("/api/auth/**").permitAll()

                                // Swagger documentation access
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**"
                                ).permitAll()

                                // Admin APIs
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                // Task APIs require login
                                .requestMatchers("/api/tasks/**").authenticated()

                                // Any other request must be authenticated
                                .anyRequest().authenticated()
                        )

                        .sessionManagement(session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )

                        .authenticationProvider(authenticationProvider)

                        // JWT filter before authentication filter
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}