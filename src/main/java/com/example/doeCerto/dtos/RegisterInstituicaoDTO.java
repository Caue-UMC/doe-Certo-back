package com.example.doeCerto.dtos;

import com.example.doeCerto.domain.CategoriasInstituicao;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record RegisterInstituicaoDTO(
        String nomeInstituicao,
        String email,
        String senha,
        CategoriasInstituicao categoria,
        String cnpj,
        String endereco,
        String telefone
) {}