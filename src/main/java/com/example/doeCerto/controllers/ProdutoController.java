package com.example.doeCerto.controllers;

import com.example.doeCerto.dtos.ProdutoRequestDTO;
import com.example.doeCerto.dtos.ProdutoResponseDTO;
import com.example.doeCerto.services.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(@RequestBody ProdutoRequestDTO produtoRequestDTO) {
        ProdutoResponseDTO novoProduto = produtoService.salvarProduto(produtoRequestDTO);
        return ResponseEntity.ok(novoProduto);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutos() {
        return ResponseEntity.ok(produtoService.listarProdutos());
    }

    @GetMapping("/instituicao/nome/{nomeInstituicao}")
    public ResponseEntity<List<ProdutoResponseDTO>> listarProdutosPorNomeInstituicao(@PathVariable String nomeInstituicao) {
        return ResponseEntity.ok(produtoService.listarProdutosPorNomeInstituicao(nomeInstituicao));
    }
}
