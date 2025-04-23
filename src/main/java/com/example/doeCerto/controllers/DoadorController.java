package com.example.doeCerto.controllers;

import com.example.doeCerto.domain.Doador;
import com.example.doeCerto.dtos.LoginRequestDTO;
import com.example.doeCerto.dtos.RegisterDoadorDTO;
import com.example.doeCerto.dtos.ResponseDTO;
import com.example.doeCerto.infra.security.TokenService;
import com.example.doeCerto.repositories.DoadorRepository;
import com.example.doeCerto.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/doadores")
@RequiredArgsConstructor

public class DoadorController {

    private final DoadorRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
        ResponseDTO response = authService.login(body);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO("Credenciais inválidas", null));
    }

    //Cadastrar um novo doador
    @PostMapping("/cadastro")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterDoadorDTO body) {
        Optional<Doador> doador = this.repository.findByEmail(body.email());

        if (doador.isEmpty()) {
            Doador newDoador = new Doador();
            newDoador.setNome(body.nome());
            newDoador.setSenha(passwordEncoder.encode(body.senha()));
            newDoador.setEmail(body.email());
            newDoador.setDt_nascimento(body.dt_nascimento());

            this.repository.save(newDoador);

            String token = this.tokenService.gerarTokenDoador(newDoador);
            return ResponseEntity.ok(new ResponseDTO(newDoador.getNome(), token));
        }
        return ResponseEntity.badRequest().body(new ResponseDTO("Doador já cadastrado.", null));
    }

    //Listar todos os doadores
    @GetMapping
    public ResponseEntity<List<Doador>> getAllDoadors() {
        List<Doador> doadores = repository.findAll();
        return ResponseEntity.ok(doadores);
    }

    //Listar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Doador> getDoadorById(@PathVariable Long id) {
        Optional<Doador> doador = repository.findById(id);
        return doador.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Listar por nome
    @GetMapping("nome/{nome}")
    public ResponseEntity<Doador> getDoadorByNome(@PathVariable String nome) {
        Optional<Doador> doador = repository.findByNome(nome);
        return doador.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Atualizar por id
    @PutMapping("/{id}")
    public ResponseEntity<Doador> updateDoador(@PathVariable Long id, @RequestBody Doador doadorDetails) {
        Optional<Doador> optionalDoador = repository.findById(id);
        if (optionalDoador.isPresent()) {
            Doador doador = optionalDoador.get();
            doador.setNome(doadorDetails.getNome());
            doador.setEmail(doadorDetails.getEmail());
            doador.setSenha(passwordEncoder.encode(doadorDetails.getSenha()));
            doador.setDt_nascimento(doadorDetails.getDt_nascimento());
            repository.save(doador);
            return ResponseEntity.ok(doador);
        }
        return ResponseEntity.notFound().build();
    }

    //Deletar por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoador(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}