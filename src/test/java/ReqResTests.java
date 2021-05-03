import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

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

//        System.out.println(response);

    }
}
