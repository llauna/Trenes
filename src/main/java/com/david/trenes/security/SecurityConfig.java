package com.david.trenes.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider tokenProvider;
    private final MongoUserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider tokenProvider, MongoUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            ObjectProvider<RateLimitingFilter> rateLimitingFilterProvider
    ) throws Exception {

        http
                // IMPORTANTE: esta cadena solo aplica a la API, no a "any request"
                .securityMatcher("/api/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/estaciones/**").permitAll()
                        .requestMatchers("/api/v1/rutas/**").permitAll()
                        .requestMatchers("/api/v1/horarios/**").permitAll()
                        .requestMatchers("/api/v1/vias/**").permitAll()
                        .requestMatchers("/api/v1/trenes/**").permitAll()

                        .requestMatchers("/api/v1/billetes/**").authenticated()
                        .requestMatchers("/api/v1/pasajeros/**").authenticated()

                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/mantenimiento/**").hasAnyRole("ADMIN", "MAINTENANCE")
                        .requestMatchers("/api/v1/incidentes/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/v1/señales/**").hasAnyRole("ADMIN", "OPERATOR")

                        .requestMatchers("/api/v1/monitoring/**").hasAnyRole("ADMIN", "OPERATOR", "MONITOR")
                        .requestMatchers("/api/v1/reports/**").hasAnyRole("ADMIN", "MANAGER")

                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        .anyRequest().authenticated()
                );

        RateLimitingFilter rateLimitingFilter = rateLimitingFilterProvider.getIfAvailable();
        if (rateLimitingFilter != null) {
            http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
        }

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}