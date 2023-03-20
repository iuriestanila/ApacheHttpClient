package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Issue;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import restAssured.AuthRAssured;
import restAssured.client.UserClientAssured;
import restAssured.client.ZipCodeClientAssured;

import java.util.List;

import static io.restassured.RestAssured.given;

public class UserURL2AssuredTest {
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

    @SneakyThrows
    @Test(description = "Scenario_1")
    public void postUserZipRemovedAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        String requestBodyJSon = userClientAssured.serializeUserInJson(userToAdd);

        Response response = given()
                .contentType(ContentType.JSON) //
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type","application/json")
                .body(requestBodyJSon)
                .when()
                .post(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT);

        List<User> users = userClientAssured.getUsersListUserAssured();
        List<String> zipCodes = zipCodeClientAssured.getZipCodesAssured();

        response.then()
                .statusCode(Const.STATUS_201); // gives 201

        softAssert.assertTrue(users.contains(userToAdd),"User wasn't added to the app");
        softAssert.assertFalse(zipCodes.contains(zipcode)); //passes
        softAssert.assertAll();
    }

    @SneakyThrows
    @Test(description = "Scenario_2")
    public void postUserAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        String requestBodyJSon = userClientAssured.serializeUserInJson(userToAdd);

        Response response = given()
                .contentType(ContentType.JSON) //
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type","application/json")
                .body(requestBodyJSon)
                .when()
                .post(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT);

        List<User> users = userClientAssured.getUsersListUserAssured();

        response.then()
                .statusCode(Const.STATUS_201);

        softAssert.assertTrue(users.contains(userToAdd),"User wasn't added to the app");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void postUserZipIncorrectAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(50,"Jonas","MALE","FFFFF");
        String requestBodyJSon = userClientAssured.serializeUserInJson(user);

        Response response = given()
                .contentType(ContentType.JSON) //
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type","application/json")
                .body(requestBodyJSon)
                .when()
                .post(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT);

        List<User> users = userClientAssured.getUsersListUserAssured();

        response.then()
                .statusCode(Const.STATUS_424);

        softAssert.assertFalse(users.contains(user),"User was added to the app");
        softAssert.assertAll();
    }

    @Issue("GML-30.1")
    @Test(description = "Scenario_4")
    public void postUserSameNameSexAsOnAppAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(50,"Maria","FEMALE", zipcode);
        String requestBodyJSon = userClientAssured.serializeUserInJson(user);

        Response response = given()
                .contentType(ContentType.JSON) //
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type","application/json")
                .body(requestBodyJSon)
                .when()
                .post(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT);

        List<User> users = userClientAssured.getUsersListUserAssured();

        response.then()
                .statusCode(Const.STATUS_400);

        softAssert.assertFalse(users.contains(user),"User was added to the app");
        softAssert.assertAll();
    }
}
