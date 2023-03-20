package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import restAssured.AuthRAssured;
import restAssured.client.UserClientAssured;
import restAssured.client.ZipCodeClientAssured;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class FilterUserURL2AssuredTest {
    private UserClient userClient;
    private UserClientAssured userClientAssured;
    private ZipCodeClientAssured zipCodeClientAssured;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToAdd;
    ObjectMapper objectMapper;

    @BeforeMethod
    public void init(){
        userClient = new UserClient();
        userClientAssured = new UserClientAssured();
        zipCodeClientAssured = new ZipCodeClientAssured();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcodeURL2();
        userToAdd = userClient.createUserDataURL2(zipcode);
        objectMapper = new ObjectMapper();
    }


    @Test(description = "Scenario_1")
    public void getUserAssuredTest() {
         Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when()
                .get(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT);

         response.then()
                 .statusCode(Const.STATUS_200);
           assertThat(response.asString(),notNullValue());
    }

    @SneakyThrows
    @Test(description = "Scenario_2")
    public void getUserOlderThanAssuredTest() {
        int ageInput = 30;
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .header("Content-Type","application/json")
                .queryParam("olderThan", ageInput)
                .when()
                .get(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT);

        String responseBody = response.getBody().asString();
        List<User> users = userClientAssured.deserializeJsonInUser(responseBody);

        response.then().statusCode(Const.STATUS_200);
        users.stream().map(User::getAge).forEach(age ->assertThat(age,greaterThan(ageInput)));
    }

    @Test(description = "Scenario_3")
    public void getUserYoungerThanAssuredTest(){
        int ageInput = 25;
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .header("Content-Type","application/json")
                .queryParam("youngerThan", ageInput)
                .when()
                .get(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT);

        String responseBody = response.getBody().asString();
        List<User> users = userClientAssured.deserializeJsonInUser(responseBody);

        response.then().statusCode(Const.STATUS_200);
        users.stream().map(User::getAge).forEach(age ->assertThat(age,lessThan(ageInput)));
    }

    @Test(description = "Scenario_4")
    public void getUserSexParameterTest(){
        String sexInput = "MALE";
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .header("Content-Type","application/json")
                .queryParam("sex", sexInput)
                .when()
                .get(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT);

        String responseBody = response.getBody().asString();
        List<User> users = userClientAssured.deserializeJsonInUser(responseBody);

        response.then().statusCode(Const.STATUS_200);
        users.stream().map(User::getSex).forEach(sex ->assertThat(sex,equalTo(sexInput)));
    }
}
