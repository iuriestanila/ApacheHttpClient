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
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import restAssured.AuthRAssured;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.not;

public class UpdateUserURL2AssuredTest {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToChange;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcode();
        userToChange = userClient.createAvailableUser(zipcode);
    }

    @Test(description = "Scenario_1")
    public void patchUserAssuredTest() {
        SoftAssert softAssert = new SoftAssert();
        User userNewValues = User.random(zipcode);
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(userToUpdate)
                .when()
                .patch(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().extract().body().jsonPath().getList("$", User.class);

        response.then()
                .statusCode(Const.STATUS_200);
        softAssert.assertTrue(users.contains(userNewValues), "User wasn't updated.");
        softAssert.assertFalse(users.contains(userToChange), "User wasn't updated.");
    }

    @Test(description = "Scenario_2")
    public void putUserIncorrectZipcodeAssuredTest() {
        SoftAssert softAssert = new SoftAssert();
        User newUser = User.random();
        UserToUpdate userToUpdate = new UserToUpdate(newUser, userToChange);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(userToUpdate)
                .when()
                .put(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .extract()
                .response();

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().extract().body().jsonPath().getList("$", User.class);

        response.then()
                .statusCode(Const.STATUS_424);
        softAssert.assertFalse(users.contains(newUser), "User was updated.");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void putUserFieldMissingAssuredTest() {
        User userNewValues = User.builder().age(25).zipCode(zipcode).build();
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        final RequestSpecification request = given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE));

        request.body(userToUpdate)
                .when()
                .put(Client.BASE_URL + Const.USERS_ENDPOINT).then().statusCode(409);

        request.when()
                .get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .body(not(Matchers.hasItem(userNewValues)));
    }
}
