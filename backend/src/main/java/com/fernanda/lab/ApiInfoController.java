package com.fernanda.lab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiInfoController {
  @GetMapping({"/", "/api"})
  public Map<String, String> info() {
    return Map.of(
        "name", "Fridge Lab API",
        "status", "ok"
    );
  }
}
