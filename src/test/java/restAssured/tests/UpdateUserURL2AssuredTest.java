package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.pojo.UserToUpdate;
import apacheHttpClient.utils.Const;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import restAssured.AuthRAssured;
import restAssured.client.UserClientAssured;

import java.util.List;

import static io.restassured.RestAssured.given;

;

public class UpdateUserURL2AssuredTest {
    UserClientAssured userClientAssured;
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToChange;

    @BeforeMethod
    public void init() {
        userClientAssured = new UserClientAssured();
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcodeURL2();
        userToChange = userClient.createAvailableUserURL2(zipcode);
    }

    @Test(description = "Scenario_1")
    public void patchUserAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        User userNewValues = User.random(zipcode);
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues,userToChange);

        String requestJson = userClientAssured.serializeUserInJson(userToUpdate);
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(requestJson)
                .when()
                .patch(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = userClientAssured.getUsersListUserAssured();

        response.then()
                .statusCode(Const.STATUS_200);
        softAssert.assertTrue(users.contains(userNewValues),"User wasn't updated.");
        softAssert.assertFalse(users.contains(userToChange),"User wasn't updated.");
    }

    @Test(description = "Scenario_2")
    public void putUserIncorrectZipcodeAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        User newUser = User.random();
        UserToUpdate userToUpdate = new UserToUpdate(newUser, userToChange);

        String requestJson = userClientAssured.serializeUserInJson(userToUpdate);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(requestJson)
                .when()
                .put(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = userClientAssured.getUsersListUserAssured();

        response.then()
                .statusCode(Const.STATUS_424);
        softAssert.assertFalse(users.contains(newUser),"User was updated.");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void putUserFieldMissingAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        User userNewValues = User.builder().age(25).zipCode(zipcode).build();
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        String requestJson = userClientAssured.serializeUserInJson(userToUpdate);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(requestJson)
                .when()
                .put(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = userClientAssured.getUsersListUserAssured();

        response.then()
                .statusCode(Const.STATUS_409);
        softAssert.assertFalse(users.contains(userNewValues),"User was updated.");
        softAssert.assertAll();
    }
}
