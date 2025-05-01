//package com.example.doeCerto.repositories;
//import com.example.doeCerto.domain.CategoriasInstituicao;
//import com.example.doeCerto.domain.Instituicao;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
//    Optional<Instituicao>findByEmail(String email);
//    List<Instituicao> findByCategoria(CategoriasInstituicao categoria);
//}
package com.example.doeCerto.repositories;

import com.example.doeCerto.domain.CategoriasInstituicao;
import com.example.doeCerto.domain.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
    Optional<Instituicao> findByEmail(String email);
    List<Instituicao> findByCategoria(CategoriasInstituicao categoria);

    // Métodos para prevenir duplicidade
    List<Instituicao> findAllByEmail(String email);
    List<Instituicao> findAllByCnpj(String cnpj);
}
