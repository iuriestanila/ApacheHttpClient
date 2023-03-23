package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import restAssured.AuthRAssured;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

;

public class DeleteUserAssuredTest {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToDelete;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcode();
        userToDelete = userClient.createAvailableUser(zipcode);
    }

    @Test(description = "DeleteUserTest scenario_1")
    public void deleteUserTest() {
        final RequestSpecification request = given().contentType(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE));

        request.body(userToDelete)
                .when().delete(Client.BASE_URL + Const.USERS_ENDPOINT).then()
                .statusCode(Const.STATUS_204);

        request.when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .body(not(Matchers.hasItem(userToDelete)));
    }


    @Test(description = "FillRequiredFieldsDeleteTest scenario_2")
    public void fillRequiredFieldsDeleteTest() {
        SoftAssert softAssert = new SoftAssert();
        User userToDeleteRequiredFields = User.builder().name(userToDelete.getName())
                .sex(userToDelete.getSex()).build();

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(userToDeleteRequiredFields)
                .when()
                .delete(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().extract().body().jsonPath().getList("$", User.class);

        List<String> zipCodes = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + ZipCodeClient.GET_ZIPCODES_ENDPOINT)
                .then().extract().body().jsonPath().getList("$");

        response.then()
                .statusCode(Const.STATUS_204);
        assertThat("User wasn't deleted.", users, not(hasItem(userToDelete)));
        softAssert.assertTrue(zipCodes.contains(userToDelete.getZipCode()),
                "Zip code wasn't returned in list of available zip codes");
        softAssert.assertAll();
    }


    @Test(description = "RequiredFieldMissDeleteTest scenario_3")
    public void requiredFieldMissDeleteTest() {
        SoftAssert softAssert = new SoftAssert();
        User userToDeleteReqFieldsMiss = User.builder().name(userToDelete.getName()).build();

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(userToDeleteReqFieldsMiss)
                .when()
                .delete(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().extract().body().jsonPath().getList("$", User.class);

        response.then()
                .statusCode(Const.STATUS_409);
        softAssert.assertTrue(users.contains(userToDelete), "User was deleted.");
        softAssert.assertAll();
    }
}
