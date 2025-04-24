package com.example.doeCerto.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LogoutController {

    @PostMapping("/sair")
    public ResponseEntity<String> logout() {
        // Só retorna 200 OK como confirmação
        return ResponseEntity.ok("Saida realizado com sucesso.");
    }
}
