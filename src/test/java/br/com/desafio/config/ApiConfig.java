package br.com.desafio.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;

public class ApiConfig {

    public static void setup() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
            .setBaseUri("https://serverest.dev")
            .addFilter(new AllureRestAssured())
            .addHeader("accept", "application/json")
            .log(LogDetail.ALL)
            .build();

        RestAssured.responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();
    }
}
