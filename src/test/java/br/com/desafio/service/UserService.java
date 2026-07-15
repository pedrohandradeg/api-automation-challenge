package br.com.desafio.service;

import br.com.desafio.model.User;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserService {

    private static final String USUARIO_ENDPOINT = "/usuarios";

    public Response listarTodosUsuarios() {
        return given()
            .when()
            .get(USUARIO_ENDPOINT);
    }

    public Response listarUsuarioPorId(String id) {
        return given()
            .pathParam("id", id)
            .when()
            .get(USUARIO_ENDPOINT + "/{id}");
    }

    public Response cadastrarUsuario(User user) {
        return given()
            .header("Content-Type", "application/json")
            .body(user)
            .when()
            .post(USUARIO_ENDPOINT);
    }

    public String cadastraRetornandoId(User user) {
        return cadastrarUsuario(user)
            .then()
            .extract()
            .path("_id");
    }

    public Response alterarUsuario(String id, User user) {
        return given()
            .header("Content-Type", "application/json")
            .body(user)
            .pathParam("id", id)
            .when()
            .put(USUARIO_ENDPOINT + "/{id}");
    }

    public Response deletarUsuario(String id) {
        return given()
            .pathParam("id", id)
            .when()
            .delete(USUARIO_ENDPOINT + "/{id}");
    }
}
