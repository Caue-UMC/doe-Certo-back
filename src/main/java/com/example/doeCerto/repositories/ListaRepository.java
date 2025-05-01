//package com.example.doeCerto.repositories;
//
//import com.example.doeCerto.domain.Lista;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.List;
//
//public interface ListaRepository extends JpaRepository<Lista, Long> {
////    List<Produto> findByInstituicao(Instituicao instituicao);
//    List<Lista> findByInstituicaoNomeInstituicaoIgnoreCaseContaining(String nome);
//}
package com.example.doeCerto.repositories;

import com.example.doeCerto.domain.Lista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ListaRepository extends JpaRepository<Lista, Long> {
    // Busca listas por nome da instituição ignorando maiúsculas/minúsculas
    List<Lista> findByInstituicaoNomeInstituicaoIgnoreCaseContaining(String nome);

    // Busca listas por ID da instituição (correto com nome idInstituicao)
    List<Lista> findByInstituicaoIdInstituicao(Long idInstituicao);
}
