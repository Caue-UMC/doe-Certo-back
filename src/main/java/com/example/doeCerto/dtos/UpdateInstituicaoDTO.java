package com.example.doeCerto.dtos;

public record UpdateInstituicaoDTO(
    String nomeInstituicao,
    String email,
    String telefone,
    String endereco,
    String categoria,
    String senhaAtual
) {}
