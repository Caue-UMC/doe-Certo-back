package com.example.doeCerto.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "instituicao")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInstituicao;
    private String nomeInstituicao;
    private String email;
    private String senha;

    @Enumerated(EnumType.STRING) // Especifica que o enum deve ser armazenado como String
    private CategoriasInstituicao categoria; // Usando o enum CategoriaInstituicao

    private String cnpj;
    private String endereco;
    private String telefone;
    private String imagemPerfil;

}