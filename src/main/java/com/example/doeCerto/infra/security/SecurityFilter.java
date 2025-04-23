package com.example.doeCerto.infra.security;


import com.auth0.jwt.exceptions.JWTCreationException;
import com.example.doeCerto.domain.Doador;
import com.example.doeCerto.domain.Instituicao;
import com.example.doeCerto.repositories.DoadorRepository;
import com.example.doeCerto.repositories.InstituicaoRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    DoadorRepository doadorRepository;

    @Autowired
    InstituicaoRepository instituicaoRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            try {
                var login = tokenService.validaToken(token);
                System.out.println("Email utilizado para acao: " + login); // Log do email

                // Tente encontrar um doador pelo email
                Doador doador = doadorRepository.findByEmail(login).orElse(null);
                if (doador != null) {
                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                    var authentication = new UsernamePasswordAuthenticationToken(doador, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // Se não for um doador, tente encontrar uma instituição
                    Instituicao instituicao = instituicaoRepository.findByEmail(login).orElse(null);
                    if (instituicao != null) {
                        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_INSTITUICAO"));
                        var authentication = new UsernamePasswordAuthenticationToken(instituicao, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        System.out.println("Usuário não encontrado: " + login); // Log de erro
                        throw new UsernameNotFoundException("Usuário não encontrado");
                    }
                }
            } catch (JWTCreationException exception) {
                throw new RuntimeException("Token inválido", exception);
            } catch (UsernameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}