package org.example.config;

import org.example.service.CustomClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomClientService customClientService;

    public SecurityConfig(CustomClientService customClientService) {
        this.customClientService = customClientService;
    }

    @Bean // шифрование пароля
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // провайдер аутентификации
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customClientService);  // Используем наш UserService
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean // настройки безопасности
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeRequests(auth -> {
                    auth.antMatchers(
                            "/", "/order", "/login", "/register", "/css/**", "/js/**", "/icon/**",
                            "/cart", "/addToCart", "/makeAnOrder", "/review", "/api/review"
                    ).permitAll();
                    auth.antMatchers(
                            "/emp"
                    ).hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("client_full_name")
                        .loginProcessingUrl("/login")
                        .permitAll()
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider());
        return http.build();
    }
}
