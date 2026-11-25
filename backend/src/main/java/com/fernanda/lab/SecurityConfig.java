package com.fernanda.lab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  private static final String[] SWAGGER_ROUTES = { "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**" };

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/api").permitAll()
            // A documentação da API só fica disponível para o professor (e só quando SWAGGER_ENABLED=true).
            .requestMatchers(SWAGGER_ROUTES).hasRole("TEACHER")
            .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // As senhas vêm de variáveis de ambiente (arquivo .env) e ficam guardadas em memória já criptografadas.
  @Bean
  InMemoryUserDetailsManager users(PasswordEncoder encoder,
      @Value("${app.users.teacher-password}") String teacherPassword,
      @Value("${app.users.student-password}") String studentPassword) {
    return new InMemoryUserDetailsManager(
        User.withUsername("teacher").password(encoder.encode(teacherPassword)).roles("TEACHER").build(),
        User.withUsername("student").password(encoder.encode(studentPassword)).roles("STUDENT").build());
  }
}
