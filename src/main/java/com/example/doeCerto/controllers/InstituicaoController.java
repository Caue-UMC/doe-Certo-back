package com.example.doeCerto.controllers;

import com.example.doeCerto.domain.CategoriasInstituicao;
import com.example.doeCerto.domain.Instituicao;
import com.example.doeCerto.dtos.*;
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
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
        Optional<Instituicao> instituicao = repository.findByEmail(body.email());
        if (instituicao.isPresent() && passwordEncoder.matches(body.senha(), instituicao.get().getSenha())) {
            String token = tokenService.gerarTokenInstituicao(instituicao.get());
            return ResponseEntity.ok(new LoginResponseDTO(token, instituicao.get().getIdInstituicao(), instituicao.get().getEmail()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

//    @PostMapping("/login")
//    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
//        ResponseDTO response = authService.login(body);
//        if (response != null) {
//            return ResponseEntity.ok(response);
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("Credenciais inválidas", null));
//    }

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
            newInstituicao.setImagemPerfil(body.imagemPerfil());
            repository.save(newInstituicao);

            String token = tokenService.gerarTokenInstituicao(newInstituicao);
            return ResponseEntity.ok(new ResponseDTO(newInstituicao.getNomeInstituicao(), token));
        }
        return ResponseEntity.badRequest().body(new ResponseDTO("Instituição já cadastrada.", null));
    }

    //Listar todas instituicoes
    @GetMapping
    public ResponseEntity<List<InstituicaoResponseDTO>> getAllInstituicoes() {
        List<InstituicaoResponseDTO> instituicoes = repository.findAll()
                .stream()
                .map(instituicao -> new InstituicaoResponseDTO(
                        instituicao.getNomeInstituicao(),
                        instituicao.getEndereco(),
                        instituicao.getTelefone(),
                        instituicao.getImagemPerfil(),
                        instituicao.getCategoria()
                ))
                .toList();
        return ResponseEntity.ok(instituicoes);
    }


    //Listar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<InstituicaoResponseDTO>> getInstituicaoByCategoria(@PathVariable String categoria) {
        try {
            CategoriasInstituicao categoriaEnum = CategoriasInstituicao.valueOf(categoria.toUpperCase());
            List<InstituicaoResponseDTO> instituicoes = repository.findByCategoria(categoriaEnum)
                    .stream()
                    .map(instituicao -> new InstituicaoResponseDTO(
                            instituicao.getNomeInstituicao(),
                            instituicao.getEndereco(),
                            instituicao.getTelefone(),
                            instituicao.getImagemPerfil(),
                            instituicao.getCategoria()

                    ))
                    .toList();
            return ResponseEntity.ok(instituicoes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Listar por ID
    @GetMapping("/{id}")
    public ResponseEntity<InstituicaoResponseDTO> getInstituicaoById(@PathVariable Long id) {
        Optional<Instituicao> optionalInstituicao = repository.findById(id);

        if (optionalInstituicao.isPresent()) {
            Instituicao instituicao = optionalInstituicao.get();
            InstituicaoResponseDTO responseDTO = new InstituicaoResponseDTO(
                    instituicao.getNomeInstituicao(),
                    instituicao.getEndereco(),
                    instituicao.getTelefone(),
                    instituicao.getImagemPerfil(),
                    instituicao.getCategoria()

            );
            return ResponseEntity.ok(responseDTO);
        }

        return ResponseEntity.notFound().build();
    }

    // Editar por ID
    @PutMapping("/{id}")
    public ResponseEntity<Instituicao> updateInstituicao(@PathVariable Long id, @RequestBody RegisterInstituicaoDTO body) {
        if (!instituicaoService.validaCategoria(String.valueOf(body.categoria()))) {
            return ResponseEntity.badRequest().body(null);
        }

        Optional<Instituicao> optionalInstituicao = repository.findById(id);
        if (optionalInstituicao.isPresent()) {
            Instituicao instituicao = optionalInstituicao.get();

            // Se nao tiver nada, nao afeta o BD
            if (body.nomeInstituicao() != null && !body.nomeInstituicao().isBlank()) {
                instituicao.setNomeInstituicao(body.nomeInstituicao());
            }
            if (body.email() != null && !body.email().isBlank()) {
                instituicao.setEmail(body.email());
            }
            if (body.cnpj() != null && !body.cnpj().isBlank()) {
                instituicao.setCnpj(body.cnpj());
            }
            if (body.telefone() != null && !body.telefone().isBlank()) {
                instituicao.setTelefone(body.telefone());
            }
            if (body.endereco() != null && !body.endereco().isBlank()) {
                instituicao.setEndereco(body.endereco());
            }
            if (body.categoria() != null) {
                instituicao.setCategoria(body.categoria());
            }
            if (body.senha() != null && !body.senha().isBlank()) {
                instituicao.setSenha(passwordEncoder.encode(body.senha()));
            }
            if (body.imagemPerfil() != null && !body.imagemPerfil().isBlank()) {
                instituicao.setImagemPerfil(body.imagemPerfil());
            }

            repository.save(instituicao);
            return ResponseEntity.ok(instituicao);
        }

        return ResponseEntity.notFound().build();
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Instituicao> updateInstituicao(@PathVariable Long id, @RequestBody RegisterInstituicaoDTO body) {
//        if (!instituicaoService.validaCategoria(String.valueOf(body.categoria()))) { // Chamando validação do serviço
//            return ResponseEntity.badRequest().body(null);
//        }
//
//        Optional<Instituicao> optionalInstituicao = repository.findById(id);
//        if (optionalInstituicao.isPresent()) {
//            Instituicao instituicao = optionalInstituicao.get();
//            instituicao.setNomeInstituicao(body.nomeInstituicao());
//            instituicao.setEmail(body.email());
//            instituicao.setSenha(body.senha());
//            instituicao.setCategoria(body.categoria());
//            instituicao.setCnpj(body.cnpj());
//            instituicao.setEndereco(body.endereco());
//            instituicao.setTelefone(body.telefone());
//            repository.save(instituicao);
//            return ResponseEntity.ok(instituicao);
//        }
//        return ResponseEntity.notFound().build();
//    }

    // Deletar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstituicao(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Para ver se funciona pegando a lista de Enum Categoria
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = List.of(CategoriasInstituicao.values())
                .stream()
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(categorias);
    }

}
