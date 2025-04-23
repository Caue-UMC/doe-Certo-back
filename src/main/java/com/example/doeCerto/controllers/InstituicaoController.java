package com.example.doeCerto.controllers;

import com.example.doeCerto.domain.CategoriasInstituicao;
import com.example.doeCerto.domain.Instituicao;
import com.example.doeCerto.dtos.LoginRequestDTO;
import com.example.doeCerto.dtos.RegisterInstituicaoDTO;
import com.example.doeCerto.dtos.ResponseDTO;
import com.example.doeCerto.infra.security.TokenService;
import com.example.doeCerto.repositories.InstituicaoRepository;
import com.example.doeCerto.services.AuthService;
import com.example.doeCerto.services.InstituicaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/instituicoes")
@RequiredArgsConstructor
public class InstituicaoController {

    private final InstituicaoRepository repository;
    private final AuthService authService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final InstituicaoService instituicaoService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
        ResponseDTO response = authService.login(body);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("Credenciais inválidas", null));
    }

    // Cadastrar nova instituicao e verifica categoria existente
    @PostMapping("/cadastro")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterInstituicaoDTO body) {

        Optional<Instituicao> instituicao = repository.findByEmail(body.email());
        if (instituicao.isEmpty()) {
            Instituicao newInstituicao = new Instituicao();
            newInstituicao.setNomeInstituicao(body.nomeInstituicao());
            newInstituicao.setEmail(body.email());
            newInstituicao.setSenha(passwordEncoder.encode(body.senha()));
            newInstituicao.setCategoria(body.categoria());
            newInstituicao.setCnpj(body.cnpj());
            newInstituicao.setEndereco(body.endereco());
            newInstituicao.setTelefone(body.telefone());
            repository.save(newInstituicao);

            String token = tokenService.gerarTokenInstituicao(newInstituicao);
            return ResponseEntity.ok(new ResponseDTO(newInstituicao.getNomeInstituicao(), token));
        }
        return ResponseEntity.badRequest().body(new ResponseDTO("Instituição já cadastrada.", null));
    }

    //Listar todas instituicoes
    @GetMapping
    public ResponseEntity<List<Instituicao>> getAllInstituicoes() {
        List<Instituicao> instituicoes = repository.findAll();
        return ResponseEntity.ok(instituicoes);
    }

    //Listar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Instituicao>> getInstituicaoByCategoria(@PathVariable String categoria) {
        try {
            CategoriasInstituicao categoriaEnum = CategoriasInstituicao.valueOf(categoria.toUpperCase());
            List<Instituicao> instituicoes = repository.findByCategoria(categoriaEnum);
            return ResponseEntity.ok(instituicoes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //Listar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Instituicao> getInstituicaoById(@PathVariable Long id) {
        Optional<Instituicao> instituicao = repository.findById(id);
        return instituicao.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Editar por ID
    @PutMapping("/{id}")
    public ResponseEntity<Instituicao> updateInstituicao(@PathVariable Long id, @RequestBody RegisterInstituicaoDTO body) {
        if (!instituicaoService.validaCategoria(String.valueOf(body.categoria()))) { // Chamando validação do serviço
            return ResponseEntity.badRequest().body(null);
        }

        Optional<Instituicao> optionalInstituicao = repository.findById(id);
        if (optionalInstituicao.isPresent()) {
            Instituicao instituicao = optionalInstituicao.get();
            instituicao.setNomeInstituicao(body.nomeInstituicao());
            instituicao.setEmail(body.email());
            instituicao.setSenha(body.senha());
            instituicao.setCategoria(body.categoria());
            instituicao.setCnpj(body.cnpj());
            instituicao.setEndereco(body.endereco());
            instituicao.setTelefone(body.telefone());
            repository.save(instituicao);
            return ResponseEntity.ok(instituicao);
        }
        return ResponseEntity.notFound().build();
    }

    // Deletar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstituicao(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
