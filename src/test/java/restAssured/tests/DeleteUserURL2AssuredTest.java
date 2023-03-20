package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import restAssured.AuthRAssured;
import restAssured.client.UserClientAssured;
import restAssured.client.ZipCodeClientAssured;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

;

public class DeleteUserURL2AssuredTest {
    private UserClient userClient;
    private UserClientAssured userClientAssured;
    ZipCodeClientAssured zipCodeClientAssured;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToDelete;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        userClientAssured = new UserClientAssured();
        zipCodeClientAssured = new ZipCodeClientAssured();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcodeURL2();
        userToDelete = userClient.createAvailableUserURL2(zipcode);
    }
    @Test(description = "Scenario_1")
    public void deleteUserTest(){
        /*
        Given I am authorized user
        When I send DELETE request to /users endpoint
        And Request body contains user to delete
        Then I get 204 response code
        And User is deleted
        And Zip code is returned in list of available zip codes
         */
        SoftAssert softAssert = new SoftAssert();

        String requestJson = userClientAssured.serializeUserInJson(userToDelete);
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type","application/json")
                .body(requestJson)
                .when()
                .delete(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = userClientAssured.getUsersListUserAssured();
        List<String> zipCodes = zipCodeClientAssured.getZipCodesAssured();

        response.then()
                .statusCode(Const.STATUS_204);
        softAssert.assertFalse(users.contains(userToDelete),"User wasn't deleted.");
        softAssert.assertTrue(zipCodes.contains(zipcode),
                "Zip code wasn't returned in list of available zip codes");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_2")
    public void fillRequiredFieldsDeleteTest(){
        /*
        Given I am authorized user
        When I send DELETE request to /users endpoint
        And Request body contains user to delete (required fields only)
        Then I get 204 response code
        And User is deleted
        And Zip code is returned in list of available zip codes
         */
        SoftAssert softAssert = new SoftAssert();
        User userToDeleteRequiredFields = User.builder().name(userToDelete.getName()).sex(userToDelete.getSex()).build();

        String requestJson = userClientAssured.serializeUserInJson(userToDeleteRequiredFields);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(requestJson)
                .when()
                .delete(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = userClientAssured.getUsersListUserAssured();
        List<String> zipCodes = zipCodeClientAssured.getZipCodesAssured();

        response.then()
                .statusCode(Const.STATUS_204);

        assertThat("User wasn't deleted.", users, not(hasItem(userToDelete)));
        softAssert.assertTrue(zipCodes.contains(userToDelete.getZipCode()),
                "Zip code wasn't returned in list of available zip codes");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void requiredFieldMissDeleteTest(){
        SoftAssert softAssert = new SoftAssert();
        User userToDeleteReqFieldsMiss = User.builder().name(userToDelete.getName()).build();

        String requestJson = userClientAssured.serializeUserInJson(userToDeleteReqFieldsMiss);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(requestJson)
                .when()
                .delete(Client.BASE_URL_2 + UserClient.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = userClientAssured.getUsersListUserAssured();

        response.then()
                .statusCode(Const.STATUS_409);

        softAssert.assertTrue(users.contains(userToDelete),"User was deleted.");
        softAssert.assertAll();
    }
}
