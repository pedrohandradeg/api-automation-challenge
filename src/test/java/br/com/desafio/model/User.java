package br.com.desafio.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String nome;
    private String email;
    private String password;
    private String administrador;
}
