package com.example.doeCerto.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "Doador")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Doador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDoador;
    private String nome;
    private String email;
    private String senha;
    private String dt_nascimento;
}