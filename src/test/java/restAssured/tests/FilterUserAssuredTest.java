package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;
import io.restassured.http.ContentType;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import restAssured.AuthRAssured;

import static io.restassured.RestAssured.given;

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


    @Test(description = "Get user test assured; scenario_1")
    public void getUserAssuredTest() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when()
                .get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .statusCode(Const.STATUS_200)
                .body(Matchers.is(Matchers.not(Matchers.empty())));
    }

    @SneakyThrows
    @Test(description = "Get user older than test assured; scenario_2")
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

    @Test(description = "Get user younger than test assured; scenario_3")
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

    @Test(description = "Get user sex parameter test assured; scenario_4")
    public void getUserSexParameterTest() {
        String sexInput = "MALE";
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .queryParam("sex", sexInput)
                .when()
                .get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .statusCode(Const.STATUS_200)
                .body("sex", Matchers.everyItem(Matchers.equalTo("MALE")));
    }
}
