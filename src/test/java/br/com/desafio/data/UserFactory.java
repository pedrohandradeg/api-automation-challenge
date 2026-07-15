package br.com.desafio.data;

import br.com.desafio.model.User;

public class UserFactory {

    public static User criarUsuarioComum() {
        return User.builder()
            .nome("Usuario Comum")
            .email("usuario.comum@email.com")
            .password("user1234")
            .administrador("false")
            .build();
    }

    public static User criarAdministrador() {
        return User.builder()
            .nome("Administrador")
            .email("usuario.admin@email.com")
            .password("admin1234")
            .administrador("true")
            .build();
    }

    public static User usuarioVazio() {
        return User.builder()
            .nome("")
            .email("")
            .password("")
            .administrador("")
            .build();
    }
}
