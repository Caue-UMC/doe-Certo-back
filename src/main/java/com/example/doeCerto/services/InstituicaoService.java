package com.example.doeCerto.services;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class InstituicaoService {

    // Lista de categoria para fazer validacao
    public boolean validaCategoria(String categoria) {

        // Lista de categorias válidas
        List<String> categoriasValidas = Arrays.asList("ONG", "ESCOLA", "HOSPITAL", "IGREJA", "CULTURA", "OUTRA");
        return categoriasValidas.contains(categoria);
    }
}