package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import restAssured.AuthRAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class FilterUserAssuredTest {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToAdd;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcode();
        userToAdd = userClient.createUserData(zipcode);
    }


    @Test(description = "GetUserTest scenario_1")
    public void getUserAssuredTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when()
                .get(Client.BASE_URL + Const.USERS_ENDPOINT);

        response.then()
                .statusCode(Const.STATUS_200);
        assertThat(response.asString(), notNullValue());
    }

    @SneakyThrows
    @Test(description = "GetUserOlderThanTest scenario_2")
    public void getUserOlderThanAssuredTest() {
        int ageInput = 30;
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .header("Content-Type", "application/json")
                .queryParam("olderThan", ageInput)
                .when()
                .get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .statusCode(Const.STATUS_200)
                .body("age", Matchers.everyItem(Matchers.greaterThan(ageInput)));
    }

    @Test(description = "GetUserYoungerThanTest scenario_3")
    public void getUserYoungerThanAssuredTest() {
        int ageInput = 30;
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .header("Content-Type", "application/json")
                .queryParam("youngerThan", ageInput)
                .when()
                .get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .statusCode(Const.STATUS_200)
                .body("age", Matchers.everyItem(Matchers.lessThan(ageInput)));
    }

    @Test(description = "GetUserSexParameterTest scenario_4")
    public void getUserSexParameterTest() {
        String sexInput = "MALE";
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .header("Content-Type", "application/json")
                .queryParam("sex", sexInput)
                .when()
                .get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .statusCode(Const.STATUS_200)
                .body("sex", Matchers.everyItem(Matchers.equalTo("MALE")));
    }
}
