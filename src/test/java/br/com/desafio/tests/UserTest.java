package br.com.desafio.tests;

import br.com.desafio.config.ApiConfig;
import br.com.desafio.data.UserFactory;
import br.com.desafio.model.User;
import br.com.desafio.service.UserService;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {

    private final UserService userService = new UserService();
    private Response response;

    @BeforeAll
    public static void setupClass() {
        ApiConfig.setup();
    }

    @Test
    @DisplayName("GET - Deve listar todos os usuários")
    public void listarTodosUsuarios() {
        response = userService.listarTodosUsuarios();

        assertEquals(200, response.statusCode());
        assertNotNull(response.path("usuarios.nome"));
        assertNotNull(response.path("usuarios.email"));
        assertNotNull(response.path("usuarios.password"));
        assertNotNull(response.path("usuarios.administrador"));
        assertNotNull(response.path("usuarios._id"));
    }

    @Test
    @DisplayName("POST - Deve cadastrar um usuário comum com sucesso")
    public void cadastrarUsuarioComSucesso() {
        response = userService.cadastrarUsuario(UserFactory.criarUsuarioComum());

        assertEquals(201, response.statusCode());
        assertNotNull(response.path("message"));
        assertNotNull(response.path("_id"));
        assertEquals("Cadastro realizado com sucesso", response.path("message"));

        userService.deletarUsuario(response.path("_id"));
    }

    @Test
    @DisplayName("POST - Deve cadastrar um administrador com sucesso")
    public void cadastrarAdminComSucesso() {
        response = userService.cadastrarUsuario(UserFactory.criarAdministrador());

        assertEquals(201, response.statusCode());
        assertNotNull(response.path("message"));
        assertNotNull(response.path("_id"));
        assertEquals("Cadastro realizado com sucesso", response.path("message"));

        userService.deletarUsuario(response.path("_id"));
    }

    @Test
    @DisplayName("POST - Não deve cadastrar usuário com email duplicado")
    public void cadastrarUsuarioComEmailDuplicado() {
        var id =  userService.cadastraRetornandoId(UserFactory.criarUsuarioComum());
        response = userService.cadastrarUsuario(UserFactory.criarUsuarioComum());

        assertEquals(400, response.statusCode());
        assertNotNull(response.path("message"));
        assertEquals("Este email já está sendo usado", response.path("message"));

        userService.deletarUsuario(id);
    }

    @Test
    @DisplayName("POST - Deve validar campos obrigatórios")
    public void validarCamposObrigatorios() {
        response = userService.cadastrarUsuario(UserFactory.usuarioVazio());

        assertEquals(400, response.statusCode());
        assertNotNull(response.path("nome"));
        assertNotNull(response.path("email"));
        assertNotNull(response.path("password"));
        assertNotNull(response.path("administrador"));
        assertEquals("nome não pode ficar em branco", response.path("nome"));
        assertEquals("email não pode ficar em branco", response.path("email"));
        assertEquals("password não pode ficar em branco", response.path("password"));
        assertEquals("administrador deve ser 'true' ou 'false'", response.path("administrador"));
    }

    @Test
    @DisplayName("GET - Deve consultar um usuário pelo ID")
    public void consultarUsuarioPeloID() {
        var id =  userService.cadastraRetornandoId(UserFactory.criarUsuarioComum());

        response = userService.listarUsuarioPorId(id);

        assertEquals(200, response.statusCode());
        assertNotNull(response.path("nome"));
        assertNotNull(response.path("email"));
        assertNotNull(response.path("password"));
        assertNotNull(response.path("administrador"));
        assertNotNull(response.path("_id"));
        assertEquals(UserFactory.criarUsuarioComum().getNome(), response.path("nome"));
        assertEquals(UserFactory.criarUsuarioComum().getEmail(), response.path("email"));
        assertEquals(UserFactory.criarUsuarioComum().getPassword(), response.path("password"));
        assertEquals(UserFactory.criarUsuarioComum().getAdministrador(), response.path("administrador"));

        userService.deletarUsuario(id);
    }

    @Test
    @DisplayName("GET - Não deve consultar usuário com id inválido")
    public void consultarUsuarioComIdInvalido() {
        var id =  "testestestestestetstests";

        response = userService.listarUsuarioPorId(id);

        assertEquals(400, response.statusCode());
        assertNotNull(response.path("id"));
        assertEquals("id deve ter exatamente 16 caracteres alfanuméricos", response.path("id"));
    }

    @Test
    @DisplayName("GET - Não deve consultar usuário inexistente")
    public void consultarUsuarioInexistente() {
        var id =  "Teste12345678910";

        response = userService.listarUsuarioPorId(id);

        assertEquals(400, response.statusCode());
        assertNotNull(response.path("message"));
        assertEquals("Usuário não encontrado", response.path("message"));
    }

    @Test
    @DisplayName("PUT - Deve alterar um usuário com sucesso")
    public void alterarUsuarioComSucesso() {
        var usuarioAlterado = User.builder()
            .nome("Usuario alterado")
            .email("usuario.alterado@email.com")
            .password("user1234")
            .administrador("true")
            .build();

        var id =  userService.cadastraRetornandoId(UserFactory.criarUsuarioComum());
        response = userService.alterarUsuario(id, usuarioAlterado);

        assertEquals(200, response.statusCode());
        assertNotNull(response.path("message"));
        assertEquals("Registro alterado com sucesso", response.path("message"));

        userService.deletarUsuario(id);
    }

    @Test
    @DisplayName("PUT - Deve validar campos obrigatórios")
    public void alterarUsuarioInexistente() {
        var id =  "Teste12345678910";
        response = userService.alterarUsuario(id, UserFactory.usuarioVazio());

        assertEquals(400, response.statusCode());
        assertNotNull(response.path("nome"));
        assertNotNull(response.path("email"));
        assertNotNull(response.path("password"));
        assertNotNull(response.path("administrador"));
        assertEquals("nome não pode ficar em branco", response.path("nome"));
        assertEquals("email não pode ficar em branco", response.path("email"));
        assertEquals("password não pode ficar em branco", response.path("password"));
        assertEquals("administrador deve ser 'true' ou 'false'", response.path("administrador"));
    }

    @Test
    @DisplayName("DELETE - Deve excluir um usuário com sucesso")
    public void excluirUsuarioComSucesso() {
        var id =  userService.cadastraRetornandoId(UserFactory.criarUsuarioComum());

        response = userService.deletarUsuario(id);

        assertEquals(200, response.statusCode());
        assertNotNull(response.path("message"));
        assertEquals("Registro excluído com sucesso", response.path("message"));
    }

    @Test
    @DisplayName("DELETE - Não deve excluir nenhum usuário inexistente")
    public void excluirUsuarioInexistente() {
        var id =  "Teste12345678910";

        response = userService.deletarUsuario(id);

        assertEquals(200, response.statusCode());
        assertNotNull(response.path("message"));
        assertEquals("Nenhum registro excluído", response.path("message"));
    }
}