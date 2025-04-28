package com.example.doeCerto.dtos;

import com.example.doeCerto.domain.StatusProduto;

public record ProdutoRequestDTO(
        String nome,
        String descricao,
        StatusProduto status,
        Long idInstituicao
) {}
