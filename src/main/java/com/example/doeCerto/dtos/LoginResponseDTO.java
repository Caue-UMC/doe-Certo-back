package com.example.doeCerto.dtos;

public record LoginResponseDTO(
        String token,
        Long id,
        String email
) {}
