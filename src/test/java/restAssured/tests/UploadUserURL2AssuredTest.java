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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import restAssured.AuthRAssured;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

public class UploadUserURL2AssuredTest {
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
    @Test(description = "Scenario_1")
    public void uploadUserAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        File file = new File("src/test/resources/users.json");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        Response response = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .contentType(ContentType.MULTIPART)
                .accept(ContentType.JSON)
                .multiPart(new File("src/test/resources/userURL2.json"))
                .when()
                .post(Client.BASE_URL + Const.USERS_UPLOAD_ENDPOINT);

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().extract().body().jsonPath().getList("$", User.class);

        response.then().statusCode(Const.STATUS_201);
        softAssert.assertTrue(equals(UPLOAD_RESPONSE_TEXT + usersFromFile.size()), response.getBody().asString());
        softAssert.assertEquals(users, usersFromFile);
    }

    @SneakyThrows
    @Test(description = "Scenario_2")
    public void uploadUserIncorrectZipcodeAssuredTest(){
        SoftAssert softAssert = new SoftAssert();
        File file = new File("src/test/resources/userIncorrectZipcode.json");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        Response response = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .contentType(ContentType.MULTIPART)
                .accept(ContentType.JSON)
                .multiPart(new File("src/test/resources/userIncorrectZipcodeURL2.json"))
                .when()
                .post(Client.BASE_URL + Const.USERS_UPLOAD_ENDPOINT);

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().extract().body().jsonPath().getList("$", User.class);

        response.then().statusCode(Const.STATUS_424);
        softAssert.assertFalse(users.containsAll(usersFromFile), "User were uploaded");
    }

    @SneakyThrows
    @Test(description = "Scenario_3")
    public void requiredFieldMissDeleteTest(){
        SoftAssert softAssert = new SoftAssert();
        File file = new File("src/test/resources/userMissRequiredFieldURL2.json");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        Response response = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .contentType(ContentType.MULTIPART)
                .accept(ContentType.JSON)
                .multiPart(new File("src/test/resources/userMissRequiredFieldURL2.json"))
                .when()
                .post(Client.BASE_URL + Const.USERS_UPLOAD_ENDPOINT);

        List<User> users = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + Const.USERS_ENDPOINT)
                .then().extract().body().jsonPath().getList("$", User.class);

        response.then().statusCode(Const.STATUS_409);
        softAssert.assertFalse(users.containsAll(usersFromFile), "User were uploaded");
        softAssert.assertAll();

    }
    @AfterMethod
    public void delete() {
        usersFromFile.forEach(user -> userClient.deleteUser(user));
    }
}
