package com.example.doeCerto.dtos;

import com.example.doeCerto.domain.StatusProduto;

public record ListaResponseDTO(
        String nome,
        String descricao,
        StatusProduto status,
        String nomeInstituicao,
        String enderecoInstituicao,
        String telefoneInstituicao,
        String imagemPerfil
) {}
