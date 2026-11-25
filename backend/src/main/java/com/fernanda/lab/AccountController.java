package com.fernanda.lab;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AccountController {
  // Usado pela tela de login: confere a senha e devolve quem é o usuário e qual o seu perfil.
  @GetMapping("/api/me")
  public Map<String, String> me(Authentication auth) {
    String role = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .map(authority -> authority.replace("ROLE_", ""))
        .findFirst()
        .orElse("");
    return Map.of("username", auth.getName(), "role", role);
  }
}
