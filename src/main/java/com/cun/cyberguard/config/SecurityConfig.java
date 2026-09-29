package com.cun.cyberguard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/favicon.svg", "/error").permitAll()
                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/reglas/*/editar").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/reglas/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/reglas/**").hasAnyRole("ADMINISTRADOR", "ANALISTA")
                        .requestMatchers("/reportes/**").hasAnyRole("ADMINISTRADOR", "ANALISTA")
                        .requestMatchers(HttpMethod.GET, "/dispositivos/nuevo", "/dispositivos/*/editar")
                        .hasAnyRole("ADMINISTRADOR", "ANALISTA")
                        .requestMatchers(HttpMethod.POST, "/dispositivos/**").hasAnyRole("ADMINISTRADOR", "ANALISTA")
                        .requestMatchers(HttpMethod.GET, "/eventos/nuevo").hasAnyRole("ADMINISTRADOR", "ANALISTA")
                        .requestMatchers(HttpMethod.POST, "/eventos/**").hasAnyRole("ADMINISTRADOR", "ANALISTA")
                        .requestMatchers(HttpMethod.POST, "/alertas/**").hasAnyRole("ADMINISTRADOR", "ANALISTA")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"));
        return http.build();
    }
}
