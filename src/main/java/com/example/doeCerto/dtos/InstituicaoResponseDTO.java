package com.example.doeCerto.dtos;

import com.example.doeCerto.domain.CategoriasInstituicao;

public record InstituicaoResponseDTO(
        String nomeInstituicao,
        String endereco,
        String telefone,
        String imagemPerfil,
        CategoriasInstituicao categoria
) {}
