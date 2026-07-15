# API Automation Challenge

Projeto de automação de testes para a API pública de gerenciamento de usuários [ServeRest](https://serverest.dev), desenvolvido como parte de um desafio técnico de automação de API.

O projeto cobre os 5 endpoints de CRUD de usuários com cenários positivos e negativos, gera relatórios de execução com o **Allure Report** e está integrado a uma pipeline de **CI no GitLab**, que roda os testes a cada atualização do repositório e publica o relatório como artefato. O código-fonte fica hospedado no **GitHub**; a execução da pipeline no GitLab acontece via espelhamento (*pull mirror*) do repositório — detalhes na seção [Pipeline de CI](#️-pipeline-de-ci).

---

## 🧰 Stack utilizada

| Ferramenta | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem |
| Maven | - | Build e gerenciamento de dependências |
| JUnit 5 (Jupiter) | 6.1.1 | Framework de testes |
| RestAssured | 6.0.0 | Cliente HTTP para testes de API |
| Jackson Databind | 3.2.1 | Serialização/deserialização JSON |
| Lombok | 1.18.46 | Redução de boilerplate (getters/setters/builders) |
| Allure Report | 2.35.3 | Geração de relatórios de execução |
| GitLab CI/CD | - | Pipeline de CI (via mirror do repositório GitHub) |

---

## 📁 Estrutura do projeto

```
src/test/java/br/com/desafio/
├── config/
│   └── ApiConfig.java        # Configuração base da API (URL, specs do RestAssured)
├── data/
│   └── UserFactory.java      # Massa de dados / builders de usuários para os testes
├── model/
│   └── User.java             # Modelo (POJO) do usuário
├── service/
│   └── UserService.java      # Camada de abstração das chamadas HTTP (GET/POST/PUT/DELETE)
└── tests/
    └── UserTest.java         # Casos de teste

src/test/resources/
└── junit-platform.properties # Habilita autodetecção de extensões (necessário para o Allure)

.gitlab-ci.yml                  # Pipeline de CI (GitLab), disparada via mirror do repositório GitHub
```

---

## ▶️ Como rodar os testes localmente

### Pré-requisitos

- Java 21 instalado (`java -version` para conferir)
- Maven instalado (`mvn -version` para conferir)
- Conexão com a internet (os testes consultam a API real em `https://serverest.dev`)

### Rodando os testes

```bash
mvn clean test
```

### Gerando e visualizando o relatório Allure

Para gerar o relatório e abrir automaticamente no navegador:

```bash
mvn clean test allure:serve
```

Para apenas gerar os arquivos estáticos do relatório (sem abrir servidor — usado também na pipeline de CI):

```bash
mvn clean test allure:report
```

O relatório estático fica disponível em `target/site/allure-maven-plugin/index.html`.

---

## ⚙️ Pipeline de CI

O código-fonte deste projeto está hospedado no **GitHub**, porém a execução da pipeline de CI acontece no **GitLab CI/CD**.

Isso é possível através do recurso **"Pull Mirroring"** do GitLab: um projeto no GitLab foi configurado para espelhar (mirror) periodicamente este repositório do GitHub. A cada atualização do espelho, o GitLab dispara automaticamente a pipeline definida em [`.gitlab-ci.yml`](.gitlab-ci.yml), que também vive dentro deste mesmo repositório.

Etapas da pipeline:

1. Configuração do JDK 21 e do Maven
2. Execução dos testes (`mvn clean test`)
3. Geração do relatório Allure (`mvn allure:report`) — executada mesmo se algum teste falhar
4. Publicação de dois artefatos da pipeline:
   - **target/site/allure-maven-plugin/**: relatório HTML navegável
   - **target/allure-results/**: resultados brutos em JSON
5. Falha do job caso algum teste tenha falhado (garantindo visibilidade do problema, mas sem perder o relatório gerado)

Para acessar o relatório de uma execução: acesse o projeto espelhado no **GitLab** → **Build → Pipelines** → selecione a execução desejada → baixe os artefatos do job na seção correspondente → abra o `index.html` do relatório localmente.

---

## ✅ Casos de teste cobertos

Todos os 5 endpoints da API de usuários (`GET /usuarios`, `POST /usuarios`, `GET /usuarios/{id}`, `PUT /usuarios/{id}`, `DELETE /usuarios/{id}`) são cobertos com cenários de sucesso e de erro:

| # | Endpoint | Cenário | Tipo |
|---|---|---|---|
| 1 | `GET /usuarios` | Deve listar todos os usuários | ✅ Positivo |
| 2 | `GET /usuarios/{id}` | Deve consultar um usuário pelo ID | ✅ Positivo |
| 3 | `GET /usuarios/{id}` | Não deve consultar usuário com ID inválido | ❌ Negativo |
| 4 | `GET /usuarios/{id}` | Não deve consultar usuário inexistente | ❌ Negativo |
| 5 | `POST /usuarios` | Deve cadastrar um administrador com sucesso | ✅ Positivo |
| 6 | `POST /usuarios` | Deve cadastrar um usuário comum com sucesso | ✅ Positivo |
| 7 | `POST /usuarios` | Deve validar campos obrigatórios (nome, email, password, administrador) | ❌ Negativo |
| 8 | `POST /usuarios` | Não deve cadastrar usuário com e-mail duplicado | ❌ Negativo |
| 9 | `PUT /usuarios/{id}` | Deve alterar um usuário com sucesso | ✅ Positivo |
| 10 | `PUT /usuarios/{id}` | Deve validar campos obrigatórios na alteração | ❌ Negativo |
| 11 | `DELETE /usuarios/{id}` | Deve excluir um usuário com sucesso | ✅ Positivo |
| 12 | `DELETE /usuarios/{id}` | Não deve excluir usuário inexistente | ❌ Negativo |

Cada teste realiza também sua própria **massa de dados** (via `UserFactory`) e **limpeza** (delete) dos registros criados, evitando poluir a base pública da API entre execuções.

---

## 📌 Observações e limitações conhecidas

- Os 5 endpoints de usuários especificados na documentação do desafio (`GET /usuarios`, `POST /usuarios`, `GET /usuarios/{id}`, `PUT /usuarios/{id}` e `DELETE /usuarios/{id}`) **não exigem autenticação via token JWT** para serem consumidos na API pública [ServeRest](https://serverest.dev). Por esse motivo, não há testes dedicados de autenticação/token nem envio de header `Authorization` nas requisições — o foco da suíte é a validação funcional do CRUD de usuários, conforme escopo definido no desafio.
- Da mesma forma, não foram identificados limites de taxa (rate limiting) reais aplicados pela API nos endpoints testados, portanto não há testes específicos de limite de requisições.
- Por ser uma API compartilhada publicamente, os dados retornados em `GET /usuarios` podem variar entre execuções (outros usuários usando a mesma API concorrentemente). Os testes foram desenhados para não depender do estado global da base, apenas dos registros criados/manipulados pelo próprio teste.

---

## 📊 Relatórios

Os relatórios de execução são gerados com **Allure Report** e incluem, para cada teste, o histórico de request/response HTTP completo (via integração `allure-rest-assured`), facilitando o diagnóstico de falhas sem precisar consultar o console.
