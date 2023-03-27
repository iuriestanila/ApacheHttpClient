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
import org.testng.Assert;
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
    @Test(description = "Post user zip removed assured test; scenario_1")
    public void postUserZipRemovedAssuredTest() {
        SoftAssert softAssert = new SoftAssert();

        given()
                .contentType(ContentType.JSON) //
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(userToAdd)
                .when()
                .post(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().statusCode(Const.STATUS_201);

        List<User> users = List.of(given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));

        List<String> zipCodes = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + ZipCodeClient.GET_ZIPCODES_ENDPOINT).then()
                .extract().body().jsonPath().getList("$");

        softAssert.assertTrue(users.contains(userToAdd), "User wasn't added to the app");
        softAssert.assertFalse(zipCodes.contains(zipcode));
        softAssert.assertAll();
    }

    @SneakyThrows
    @Test(description = "Post user assured test; scenario_2")
    public void postUserAssuredTest() {
        given()
                .contentType(ContentType.JSON) //
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(userToAdd)
                .when()
                .post(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then()
                .statusCode(Const.STATUS_201);

        List<User> users = List.of(given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));

        Assert.assertTrue(users.contains(userToAdd), "User wasn't added to the app");
    }

    @Test(description = "Post user zip incorrect assured test; scenario_3")
    public void postUserZipIncorrectAssuredTest() {
        String randomZipcode = zipcodeClient.createRandomZipcode();
        User user = userClient.createUserData(randomZipcode);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().statusCode(Const.STATUS_424);

        List<User> users = List.of(given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));

        Assert.assertFalse(users.contains(user), "User was added to the app");
    }

    @SneakyThrows
    @Test(description = "Post user same name sex as on app assured test; scenario_4")
    public void postUserSameNameSexAsOnAppAssuredTest() {
        List<User> usersAll = List.of(given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));
        User userToUse = usersAll.get(0);

        User userToPost = new User(50, userToUse.getName(), userToUse.getSex(), zipcode);
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(userToPost)
                .when()
                .post(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().statusCode(Const.STATUS_400);

        List<User> users = List.of(given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));

        Assert.assertFalse(users.contains(userToPost), "User was added to the app");
    }
}
