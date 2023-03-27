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
import org.testng.Assert;
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

    @Test(description = "Delete user assured test; scenario_1")
    public void deleteUserTest() {
        given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(userToDelete)
                .delete(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().statusCode(204);

        final List<User> users = List.of(given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));

        given().header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .get(Client.BASE_URL + "/zip-codes")
                .then()
                .body(Matchers.containsString(zipcode));

        Assert.assertFalse(users.contains(userToDelete));
    }

    @Test(description = "Fill required fields delete assured test; scenario_2")
    public void fillRequiredFieldsDeleteTest() {
        User userToDeleteRequiredFields = User.builder().name(userToDelete.getName())
                .sex(userToDelete.getSex()).build();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(userToDeleteRequiredFields)
                .when()
                .delete(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().statusCode(Const.STATUS_204);

        List<User> users = List.of(given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));

        given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when()
                .get(Client.BASE_URL + ZipCodeClient.GET_ZIPCODES_ENDPOINT)
                .then()
                .body(Matchers.containsString(zipcode));

        assertThat("User wasn't deleted.", users, not(hasItem(userToDelete)));
    }

    @Test(description = "Required field miss delete assured test; scenario_3")
    public void requiredFieldMissDeleteTest() {
        User userToDeleteReqFieldsMiss = User.builder().name(userToDelete.getName()).build();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .body(userToDeleteReqFieldsMiss)
                .when()
                .delete(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().statusCode(Const.STATUS_409);

        final List<User> users = List.of(given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .as(User[].class));

        Assert.assertTrue(users.contains(userToDelete), "User was deleted.");
    }
}
