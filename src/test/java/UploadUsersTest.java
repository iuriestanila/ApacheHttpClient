import client.UserClient;
import client.ZipCodeClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pojo.ResponseEntity;
import pojo.User;
import utils.Const;

import java.io.File;
import java.util.List;

public class UploadUsersTest {
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
    public void uploadUserTest() {
        SoftAssert softAssert = new SoftAssert();
        File file = new File("src/test/resources/users.json ");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        ResponseEntity<String> responseEntity = userClient.postUsers(file);
        List<User> users = userClient.getUsers().getBody();

        softAssert.assertEquals(responseEntity.getStatusCode(), Const.STATUS_201);
        softAssert.assertTrue(responseEntity.getBody().equals(UPLOAD_RESPONSE_TEXT + usersFromFile.size()));
        softAssert.assertEquals(users, usersFromFile);
        softAssert.assertAll();
    }

    @SneakyThrows
    @Test(description = "Scenario_2; bug")
    public void uploadUserIncorrectZipcodeTest() {
        SoftAssert softAssert = new SoftAssert();
        File file = new File("src/test/resources/userIncorrectZipcode.json");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        ResponseEntity<String> responseEntity = userClient.postUsers(file);
        List<User> users = userClient.getUsers().getBody();

        softAssert.assertEquals(responseEntity.getStatusCode(), Const.STATUS_424, "Wrong status code.");
        softAssert.assertFalse(users.containsAll(usersFromFile), "User were uploaded");
        softAssert.assertAll();
    }

    @SneakyThrows
    @Test(description = "Scenario_3, bug")
    public void requiredFieldMissDeleteTest() {
        SoftAssert softAssert = new SoftAssert();
        File file = new File("src/test/resources/userIncorrectZipcode.json");
        usersFromFile = objectMapper.readValue(file, new TypeReference<>() {
        });

        ResponseEntity<String> responseEntity = userClient.postUsers(file);
        List<User> users = userClient.getUsers().getBody();

        softAssert.assertEquals(responseEntity.getStatusCode(), Const.STATUS_409, "Wrong status code.");
        softAssert.assertFalse(users.containsAll(usersFromFile), "Users were uploaded");
        softAssert.assertAll();
    }

    @AfterMethod
    public void delete() {
        usersFromFile.forEach(user -> userClient.deleteUser(user));
    }
}
