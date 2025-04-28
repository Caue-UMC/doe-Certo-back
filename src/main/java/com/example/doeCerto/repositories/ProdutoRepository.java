package com.example.doeCerto.repositories;

import com.example.doeCerto.domain.Produto;
import com.example.doeCerto.domain.Instituicao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
//    List<Produto> findByInstituicao(Instituicao instituicao);
    List<Produto> findByInstituicaoNomeInstituicaoIgnoreCaseContaining(String nome);
}
