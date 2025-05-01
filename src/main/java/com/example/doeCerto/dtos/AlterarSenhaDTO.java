package com.example.doeCerto.dtos;

import javax.validation.constraints.NotBlank;

public record AlterarSenhaDTO(
        @NotBlank String senhaAtual,
        @NotBlank String novaSenha
) {}
