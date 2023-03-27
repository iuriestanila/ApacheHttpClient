package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.UserClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import restAssured.AuthRAssured;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

public class UploadUserAssuredTest {
    private static final String UPLOAD_RESPONSE_TEXT = "Number of users = ";
    private UserClient userClient;
    private ObjectMapper objectMapper;
    List<User> usersFromFile;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Test(description = "Upload user assured test; scenario_1")
    public void uploadUserAssuredTest() {
        SoftAssert softAssert = new SoftAssert();
        File file = new File("src/test/resources/userURL2.json");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        Response response = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .contentType(ContentType.MULTIPART)
                .accept(ContentType.JSON)
                .multiPart(new File("src/test/resources/userURL2.json"))
                .when()
                .post(Client.BASE_URL + Const.USERS_UPLOAD_ENDPOINT);

        final List<User> users = List.of(given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));

        response.then().statusCode(Const.STATUS_201);
        softAssert.assertTrue(response.getBody().asString().equals(UPLOAD_RESPONSE_TEXT + usersFromFile.size()));
        softAssert.assertEquals(users, usersFromFile);
        softAssert.assertAll();
    }

    @SneakyThrows
    @Test(description = "Upload user incorrect zipcode assured test; scenario_2")
    public void uploadUserIncorrectZipcodeAssuredTest() {
        File file = new File("src/test/resources/userIncorrectZipcode.json");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .contentType(ContentType.MULTIPART)
                .accept(ContentType.JSON)
                .multiPart(new File("src/test/resources/userIncorrectZipcodeURL2.json"))
                .when()
                .post(Client.BASE_URL + Const.USERS_UPLOAD_ENDPOINT)
                .then().statusCode(Const.STATUS_424);

        final List<User> users = List.of(given().contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .get(Client.BASE_URL + Const.USERS_ENDPOINT).as(User[].class));

        Assert.assertFalse(users.containsAll(usersFromFile), "User were uploaded");
    }

    @SneakyThrows
    @Test(description = "Required field miss upload assured test; scenario_3")
    public void requiredFieldMissDeleteTest() {
        File file = new File("src/test/resources/userMissRequiredFieldURL2.json");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .contentType(ContentType.MULTIPART)
                .accept(ContentType.JSON)
                .multiPart(new File("src/test/resources/userMissRequiredFieldURL2.json"))
                .when()
                .post(Client.BASE_URL + Const.USERS_UPLOAD_ENDPOINT)
                .then().statusCode(Const.STATUS_409);

        final List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().extract().body().jsonPath().getList("$", User.class);

        Assert.assertFalse(users.containsAll(usersFromFile), "Users were uploaded");
    }

    @AfterMethod
    public void delete() {
        usersFromFile.forEach(user -> userClient.deleteUser(user));
    }
}
