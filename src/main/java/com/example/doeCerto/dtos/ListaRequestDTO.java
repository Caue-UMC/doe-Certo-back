package com.example.doeCerto.dtos;

import com.example.doeCerto.domain.StatusProduto;

public record ListaRequestDTO(
        String nome,
        String descricao,
        StatusProduto status,
        Long idInstituicao
) {}
