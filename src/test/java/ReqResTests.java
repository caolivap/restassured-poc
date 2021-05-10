import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;

public class ReqResTests {

    @BeforeEach
    public void Setup() {
        RestAssured.baseURI = "https://reqres.in/";
        RestAssured.basePath = "/api";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void loginTest() {
        String response = given()
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("login")
                .then()
                .extract()
                .asString();

    }

    @Test
    public void loginTestWithAssetion() {

        given()
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("login")
                .then()
                .statusCode(200)
                .body("token", equalToIgnoringCase("QpwL5tke4Pnpja7X4"));

    }

    @Test
    public void getSingleUserTest() {

        given()
                .get("users/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2));

    }

    @Test
    public void deleteUserTest() {

        given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT); //Podíamos haber puesto simplemente 204
        // pero aprovechamos las constantes proporcionadas por la interfaz HttpStatus

    }

    @Test
    public void patchUserTest() {

        String jobUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("job");

        assertThat(jobUpdated, equalTo("zion resident"));

    }

    @Test
    public void putUserTest() {

        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("name");

        assertThat(nameUpdated, equalTo("morpheus"));

    }

    @Test
    public void getAllUsersTest() {
        Response response = given()
                .get("users?page=2");

        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.getContentType();

        assertThat(statusCode, equalTo(HttpStatus.SC_OK));
        System.out.println("body: " + body);
        System.out.println("contentType: " + contentType);
        System.out.println("Headers: " + headers);

        System.out.println(headers.get("Content-Type"));
        System.out.println(headers.get("Transfer-Encoding"));

    }

    @Test
    public void getAllUsersTest2() {
        //Below we'll use method from of the class JsonPath
        String response = given()
                .get("users?page=2")
                .then()
                .extract()
                .body().asString();

        int page = from(response).get("page");
        int total_page = from(response).get("total_pages");
        int idFirstUser = from(response).get("data[0].id");

        System.out.println("page: " + page);
        System.out.println("total pages: " + total_page);
        System.out.println("idFirstUser: " + idFirstUser);

        List<Map> usersWithIdGreaterThan10 = from(response).get("data.findAll {user -> user.id > 10}");
        usersWithIdGreaterThan10.forEach(System.out::println);

        List<Map> user = from(response).get("data.findAll {user -> user.id > 10 && user.last_name == 'Howell'}");
        System.out.println(user);
    }

    @Test
    public void createUserTest() {

        //In this test, we need add plugin RoboPOJOGenerator in Intellij Idea
        //And to add Jackson dependency in our build.gradle

        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users")
                .then()
                .extract()
                .body().asString();

        User user = from(response).getObject("", User.class);
        System.out.println(user.getName());
        System.out.println(user.getId());
        System.out.println(user.getJob());
        System.out.println(user.getCreatedAt());
    }

    @Test
    public void registerUserTest() {

        CreateUserRequest user = new CreateUserRequest();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("pistol");

        CreateUserResponse userResponse =
                given()
                        .when()
                        .body(user)
                        .post("register")
                        .then()
                        .statusCode(200) // Antes de mapear la respuesta a nuestro objeto, primero validamos que
                        .contentType("application/json; charset=utf-8") // nuestra petición fue respondida correctamente y que el response trae información
                        .extract()
                        .body()
                        .as(CreateUserResponse.class);

        assertThat(userResponse.getId(), equalTo(4));
        assertThat(userResponse.getToken(), equalTo("QpwL5tke4Pnpja7X4"));
    }


}
