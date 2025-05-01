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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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

    // Endpoint de login para instituições
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
        Optional<Instituicao> instituicao = repository.findByEmail(body.email());
        if (instituicao.isPresent() && passwordEncoder.matches(body.senha(), instituicao.get().getSenha())) {
            String token = tokenService.gerarTokenInstituicao(instituicao.get());
            return ResponseEntity.ok(new LoginResponseDTO(token, instituicao.get().getIdInstituicao(), instituicao.get().getEmail()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Cadastrar nova instituição com verificação de duplicidade
    @PostMapping("/cadastro")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterInstituicaoDTO body) {
        // Verifica duplicidade por email e me ajuda no front
        List<Instituicao> byEmail = repository.findAllByEmail(body.email());
        if (!byEmail.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseDTO("Email já cadastrado.", null));
        }

        // Verifica duplicidade por CNPJ e me ajuda no front
        List<Instituicao> byCnpj = repository.findAllByCnpj(body.cnpj());
        if (!byCnpj.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseDTO("CNPJ já cadastrado.", null));
        }

        // Continua o cadastro
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

    // Lista todas as instituições
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

    // Lista instituições por categoria
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

    // Busca instituição por ID (com validação do token)
    @GetMapping("/{id}")
    public ResponseEntity<InstituicaoResponseDTO> getInstituicaoById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        String emailToken = tokenService.validaToken(token.replace("Bearer ", ""));
        Optional<Instituicao> optionalInstituicao = repository.findById(id);

        if (optionalInstituicao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Instituicao instituicao = optionalInstituicao.get();

        if (!instituicao.getEmail().equals(emailToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        InstituicaoResponseDTO responseDTO = new InstituicaoResponseDTO(
                instituicao.getNomeInstituicao(),
                instituicao.getEndereco(),
                instituicao.getTelefone(),
                instituicao.getImagemPerfil(),
                instituicao.getCategoria()
        );
        return ResponseEntity.ok(responseDTO);
    }

    // Atualiza dados da instituição (com verificação de senha)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInstituicao(
            @PathVariable Long id,
            @RequestBody @Valid UpdateInstituicaoDTO body,
            @RequestHeader("Authorization") String token
    ) {
        String emailToken = tokenService.validaToken(token.replace("Bearer ", ""));
        Optional<Instituicao> optionalInstituicao = repository.findById(id);

        if (optionalInstituicao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Instituicao instituicao = optionalInstituicao.get();

        if (!instituicao.getEmail().equals(emailToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!passwordEncoder.matches(body.senhaAtual(), instituicao.getSenha())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Senha atual incorreta");
        }

        if (body.nomeInstituicao() != null && !body.nomeInstituicao().isBlank()) {
            instituicao.setNomeInstituicao(body.nomeInstituicao());
        }
        if (body.email() != null && !body.email().isBlank()) {
            instituicao.setEmail(body.email());
        }
        if (body.telefone() != null && !body.telefone().isBlank()) {
            instituicao.setTelefone(body.telefone());
        }
        if (body.endereco() != null && !body.endereco().isBlank()) {
            instituicao.setEndereco(body.endereco());
        }
        // Agora trata a categoria recebida como string
        if (body.categoria() != null && !body.categoria().isBlank()) {
            try {
                CategoriasInstituicao categoriaEnum = CategoriasInstituicao.valueOf(body.categoria().toUpperCase());
                instituicao.setCategoria(categoriaEnum);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Categoria inválida.");
            }
        }

        repository.save(instituicao);
        return ResponseEntity.ok().build();
    }

    // Deleta instituição com confirmação de senha
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInstituicao(
            @PathVariable Long id,
            @RequestBody(required = false) SenhaConfirmacaoDTO body,
            @RequestHeader("Authorization") String token
    ) {
        String emailToken = tokenService.validaToken(token.replace("Bearer ", ""));
        Optional<Instituicao> optionalInstituicao = repository.findById(id);

        if (optionalInstituicao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Instituicao instituicao = optionalInstituicao.get();

        if (!instituicao.getEmail().equals(emailToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (body == null || body.senha() == null || !passwordEncoder.matches(body.senha(), instituicao.getSenha())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Senha incorreta");
        }

        repository.delete(instituicao);
        return ResponseEntity.noContent().build();
    }

    // Lista todas as categorias disponíveis (enums)
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = List.of(CategoriasInstituicao.values())
                .stream()
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(categorias);
    }

    // Altera a senha da instituição
    @PutMapping("/{id}/alterar-senha")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable Long id,
            @RequestBody @Valid AlterarSenhaDTO body
    ) {
        Optional<Instituicao> optionalInstituicao = repository.findById(id);

        if (optionalInstituicao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Instituicao instituicao = optionalInstituicao.get();

        // Valida se a senha atual está correta
        if (!passwordEncoder.matches(body.senhaAtual(), instituicao.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Atualiza para a nova senha criptografada
        instituicao.setSenha(passwordEncoder.encode(body.novaSenha()));
        repository.save(instituicao);

        return ResponseEntity.noContent().build();
    }


    //Add Imagem de perfil
    @PutMapping("/{id}/imagem")
    public ResponseEntity<?> atualizarImagemPerfil(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem,
            @RequestHeader("Authorization") String token
    ) {
        String emailToken = tokenService.validaToken(token.replace("Bearer ", ""));
        Optional<Instituicao> optionalInstituicao = repository.findById(id);

        if (optionalInstituicao.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Instituicao instituicao = optionalInstituicao.get();

        if (!instituicao.getEmail().equals(emailToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            byte[] imagemBytes = imagem.getBytes();
            String base64Imagem = Base64.getEncoder().encodeToString(imagemBytes);
            instituicao.setImagemPerfil(base64Imagem);
            repository.save(instituicao);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a imagem");
        }
    }

}
