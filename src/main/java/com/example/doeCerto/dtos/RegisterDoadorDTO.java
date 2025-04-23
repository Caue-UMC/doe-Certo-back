package com.example.doeCerto.dtos;


import javax.validation.constraints.NotBlank;

public record RegisterDoadorDTO(
        String nome,
        String email,
        String senha,
        String dt_nascimento
) {}
