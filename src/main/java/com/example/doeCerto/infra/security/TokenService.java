package com.example.doeCerto.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.example.doeCerto.domain.Doador;
import com.example.doeCerto.domain.Instituicao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarTokenDoador(Doador doador) {
        return gerarToken(doador.getEmail(), "Doador");
    }

    public String gerarTokenInstituicao(Instituicao instituicao) {
        return gerarToken(instituicao.getEmail(), "Instituicao");
    }

    private String gerarToken(String subject, String issuer) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(subject)
                    .withExpiresAt(this.generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public String validaToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token não pode ser nulo ou vazio");
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String issuer = JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getIssuer();

            // Verificar se o emissor é válido
            if (!"Doador".equals(issuer) && !"Instituicao".equals(issuer)) {
                throw new RuntimeException("Token inválido: emissor não reconhecido");
            }

            return JWT.require(algorithm)
                    .withIssuer(issuer) // Validar com o emissor obtido
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Token inválido", exception);
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }
}