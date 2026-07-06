package com.minimarket.security.config;

import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de Spring Security para la actividad de la Semana 6.
 *
 * <p>Habilita la seguridad a nivel de metodo ({@link EnableMethodSecurity}) para
 * poder usar {@code @PreAuthorize} con control de acceso por rol en los
 * controladores (Producto solo ADMIN, Inventario ADMIN/BODEGUERO, Venta CAJERO).
 * El endpoint de autenticacion {@code /api/auth/login} y la consola H2 son
 * publicos; el resto requiere autenticacion. Se usa autenticacion HTTP Basic
 * para que las pruebas de autorizacion sean directas y reproducibles.
 */
@Configuration
@EnableMethodSecurity // habilita @PreAuthorize en los controladores
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publicos: login, recurso de bienvenida, consola H2 y Swagger (desarrollo).
                        .requestMatchers("/api/auth/**", "/public/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // El resto requiere autenticacion; el control fino por rol se aplica
                        // con @PreAuthorize en cada controlador.
                        .anyRequest().authenticated()
                )
                // Permite que la consola H2 se renderice dentro de un frame del mismo origen.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // Autenticacion HTTP Basic (simple y verificable en las pruebas).
                .httpBasic(httpBasic -> {});

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    /**
     * Provider que delega la carga de usuarios en CustomUserDetailsService y la
     * verificacion de contrasenas en BCryptPasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // hash unidireccional con salt automatico
    }
}
