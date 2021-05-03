import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class ReqResTests {

    @Test
    public void loginTest() {

        String response = RestAssured
                .given()
                .log().all() //Para loggear lo que se está enviando
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("https://reqres.in/api/login")
                .then()
                .log().all() //Para loggear lo que se recibe
                .extract()
                .asString();

    }

    @Test
    public void loginTestWithAssetion() {

        RestAssured
                .given()
                .log().all() //Para loggear lo que se está enviando
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("https://reqres.in/api/login")
                .then()
                .log().all() //Para loggear lo que se recibe
                .statusCode(200)
                .body("token", equalToIgnoringCase("QpwL5tke4Pnpja7X4"));

    }

    @Test
    public void getSingleUserTest() {

        RestAssured
                .given()
                .log().all() //Para loggear lo que se está enviando
                .contentType(ContentType.JSON)
                .get("https://reqres.in/api/users/2")
                .then()
                .log().all() //Para loggear lo que se recibe
                .statusCode(200)
                .body("data.id", equalTo(2));

    }


}
