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
import org.testng.asserts.SoftAssert;
import restAssured.AuthRAssured;

import java.util.List;

import static io.restassured.RestAssured.given;

public class UserAssuredTest {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToAdd;
    ObjectMapper objectMapper;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcode();
        userToAdd = userClient.createUserData(zipcode);
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Test(description = "PostUserZipRemovedAssuredTest scenario_1")
    public void postUserZipRemovedAssuredTest() {
        SoftAssert softAssert = new SoftAssert();

        Response response = given()
                .contentType(ContentType.JSON) //
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(userToAdd)
                .when()
                .post(Client.BASE_URL + Const.USERS_ENDPOINT);

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).then().extract()
                .body()
                .jsonPath().getList("$", User.class);

        List<String> zipCodes = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + ZipCodeClient.GET_ZIPCODES_ENDPOINT).then()
                .extract().body().jsonPath().getList("$");

        response.then()
                .statusCode(Const.STATUS_201);

        softAssert.assertTrue(users.contains(userToAdd), "User wasn't added to the app");
        softAssert.assertFalse(zipCodes.contains(zipcode));
        softAssert.assertAll();
    }

    @SneakyThrows
    @Test(description = "PostUserAssuredTest scenario_2")
    public void postUserAssuredTest() {
        SoftAssert softAssert = new SoftAssert();

        Response response = given()
                .contentType(ContentType.JSON) //
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(userToAdd)
                .when()
                .post(Client.BASE_URL + Const.USERS_ENDPOINT);

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).then().extract()
                .body()
                .jsonPath().getList("$", User.class);

        response.then()
                .statusCode(Const.STATUS_201);

        softAssert.assertTrue(users.contains(userToAdd), "User wasn't added to the app");
        softAssert.assertAll();
    }

    @Test(description = "PostUserZipIncorrectAssuredTest scenario_3")
    public void postUserZipIncorrectAssuredTest() {
        SoftAssert softAssert = new SoftAssert();
        String randomZipcode = zipcodeClient.createRandomZipcode();
        User user = userClient.createUserData(randomZipcode);

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(Client.BASE_URL + Const.USERS_ENDPOINT);

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).then().extract()
                .body()
                .jsonPath().getList("$", User.class);

        response.then()
                .statusCode(Const.STATUS_424);

        softAssert.assertFalse(users.contains(user), "User was added to the app");
        softAssert.assertAll();
    }

    @SneakyThrows
    @Test(description = "PostUserSameNameSexAsOnAppAssuredTest scenario_4")
    public void postUserSameNameSexAsOnAppAssuredTest() {
        SoftAssert softAssert = new SoftAssert();
        List<User> usersAll = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).then().extract()
                .body()
                .jsonPath().getList("$", User.class);
        User userToUse = usersAll.get(0);

        User userToPost = new User(50, userToUse.getName(), userToUse.getSex(), zipcode);
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(userToPost)
                .when()
                .post(Client.BASE_URL + Const.USERS_ENDPOINT);

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).then().extract()
                .body()
                .jsonPath().getList("$", User.class);

        response.then()
                .statusCode(Const.STATUS_400);

        softAssert.assertFalse(users.contains(userToPost), "User was added to the app");
        softAssert.assertAll();
    }
}