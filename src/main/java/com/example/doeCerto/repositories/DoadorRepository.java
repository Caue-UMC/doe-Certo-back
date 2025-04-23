package com.example.doeCerto.repositories;
import com.example.doeCerto.domain.Doador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoadorRepository extends JpaRepository<Doador, Long> {
    Optional<Doador> findByEmail(String email);
    Optional<Doador> findByNome(String nome);
}
