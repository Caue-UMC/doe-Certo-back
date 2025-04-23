package com.example.doeCerto.infra.security;

import com.example.doeCerto.domain.Doador;
import com.example.doeCerto.domain.Instituicao;
import com.example.doeCerto.repositories.DoadorRepository;
import com.example.doeCerto.repositories.InstituicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Component

public class CustomUserDetailsService implements UserDetailsService {

    private final DoadorRepository doadorRepository;
    private final InstituicaoRepository instituicaoRepository;

    @Autowired
    public CustomUserDetailsService(DoadorRepository doadorRepository, InstituicaoRepository instituicaoRepository) {
        this.doadorRepository = doadorRepository;
        this.instituicaoRepository = instituicaoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Tentar encontrar um doador pelo email
        Optional<Doador> doadorOptional = doadorRepository.findByEmail(username);
        if (doadorOptional.isPresent()) {
            Doador doador = doadorOptional.get();
            return new org.springframework.security.core.userdetails.User(
                    doador.getEmail(),
                    doador.getSenha(),
                    new ArrayList<>()
            );
        }

        // Tentar encontrar uma instituição pelo email
        Optional<Instituicao> instituicaoOptional = instituicaoRepository.findByEmail(username);
        if (instituicaoOptional.isPresent()) {
            Instituicao instituicao = instituicaoOptional.get();
            return new org.springframework.security.core.userdetails.User(
                    instituicao.getEmail(),
                    instituicao.getSenha(),
                    new ArrayList<>()
            );
        }

        // Se não encontrar nenhum usuário
        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}
