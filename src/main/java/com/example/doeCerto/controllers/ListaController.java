//package com.example.doeCerto.controllers;
//
//import com.example.doeCerto.domain.StatusProduto;
//import com.example.doeCerto.dtos.ListaRequestDTO;
//import com.example.doeCerto.dtos.ListaResponseDTO;
//import com.example.doeCerto.services.ListaService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//@RestController
//@RequestMapping("/lista")
//public class ListaController {
//
//    private final ListaService listaService;
//
//    public ListaController(ListaService listaService) {
//        this.listaService = listaService;
//    }
//
//    //Cadastro de nova lista
//    @PostMapping
//    public ResponseEntity<ListaResponseDTO> cadastrarProduto(@RequestBody ListaRequestDTO listaRequestDTO) {
//        ListaResponseDTO novoProduto = listaService.salvarProduto(listaRequestDTO);
//        return ResponseEntity.ok(novoProduto);
//    }
//
//    //Lista de lista
//    @GetMapping("instituicao/${id}")
//    public ResponseEntity<List<ListaResponseDTO>> listarProdutos() {
//        return ResponseEntity.ok(listaService.listarProdutos());
//    }
//
//    //Lista de lista por nome de instituicao
//    @GetMapping("/instituicao/lista/{nomeInstituicao}")
//    public ResponseEntity<List<ListaResponseDTO>> listarProdutosPorNomeInstituicao(@PathVariable String nomeInstituicao) {
//        return ResponseEntity.ok(listaService.listarProdutosPorNomeInstituicao(nomeInstituicao));
//    }
//
//    //Lista todos os status disponíveis (enums)
//    @GetMapping("/status")
//    public ResponseEntity<List<String>> listarStatus() {
//        List<String> status = Arrays.stream(StatusProduto.values())
//                .map(Enum::name)
//                .toList();
//        return ResponseEntity.ok(status);
//    }
//
//}
package com.example.doeCerto.controllers;

import com.example.doeCerto.domain.StatusProduto;
import com.example.doeCerto.dtos.ListaRequestDTO;
import com.example.doeCerto.dtos.ListaResponseDTO;
import com.example.doeCerto.services.ListaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/lista")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    // Cadastro de nova lista
    @PostMapping
    public ResponseEntity<ListaResponseDTO> cadastrarProduto(@RequestBody ListaRequestDTO listaRequestDTO) {
        ListaResponseDTO novoProduto = listaService.salvarProduto(listaRequestDTO);
        return ResponseEntity.ok(novoProduto);
    }

    // Lista de produtos por ID da instituição
    @GetMapping("/instituicao/{id}")
    public ResponseEntity<List<ListaResponseDTO>> listarPorInstituicao(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.listarProdutosPorInstituicao(id));
    }

    // Lista de lista por nome de instituicao
    @GetMapping("/instituicao/lista/{nomeInstituicao}")
    public ResponseEntity<List<ListaResponseDTO>> listarProdutosPorNomeInstituicao(@PathVariable String nomeInstituicao) {
        return ResponseEntity.ok(listaService.listarProdutosPorNomeInstituicao(nomeInstituicao));
    }

    // Lista todos os status disponíveis (enums)
    @GetMapping("/status")
    public ResponseEntity<List<String>> listarStatus() {
        List<String> status = Arrays.stream(StatusProduto.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(status);
    }
}
