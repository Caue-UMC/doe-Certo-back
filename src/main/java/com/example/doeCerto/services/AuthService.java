package com.example.doeCerto.services;

import com.example.doeCerto.domain.Doador;
import com.example.doeCerto.domain.Instituicao;
import com.example.doeCerto.dtos.LoginRequestDTO;
import com.example.doeCerto.dtos.ResponseDTO;
import com.example.doeCerto.repositories.DoadorRepository;
import com.example.doeCerto.repositories.InstituicaoRepository;
import com.example.doeCerto.infra.security.TokenService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final DoadorRepository doadorRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Autowired
    public AuthService(DoadorRepository doadorRepository, InstituicaoRepository instituicaoRepository,
                       PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.doadorRepository = doadorRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public ResponseDTO login(LoginRequestDTO body) {
        // Tentar encontrar um doador pelo email
        Optional<Doador> doadorOptional = doadorRepository.findByEmail(body.email());
        if (doadorOptional.isPresent()) {
            Doador doador = doadorOptional.get();
            if (passwordEncoder.matches(body.senha(), doador.getSenha())) {
                String token = tokenService.gerarTokenDoador(doador);
                return new ResponseDTO(doador.getNome(), token);
            }
        }
        // Tentar encontrar uma instituição pelo email
        Optional<Instituicao> instituicaoOptional = instituicaoRepository.findByEmail(body.email());
        if (instituicaoOptional.isPresent()) {
            Instituicao instituicao = instituicaoOptional.get();
            if (passwordEncoder.matches(body.senha(), instituicao.getSenha())) {
                String token = tokenService.gerarTokenInstituicao(instituicao);
                return new ResponseDTO(instituicao.getNomeInstituicao(), token);
            }
        }
        // Se não encontrar nenhum usuário ou as credenciais estiverem incorretas
        return null;
    }
}