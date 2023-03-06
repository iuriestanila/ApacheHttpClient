import client.UserClient;
import client.ZipCodeClient;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pojo.User;
import pojo.UserToUpdate;
import utils.Const;

import java.util.List;

public class UpdateUserTest {
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
    public void patchUserTest() {
        SoftAssert softAssert = new SoftAssert();

        User userNewValues = User.random(zipcode);
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        int statusCode = userClient.patchUser(userToUpdate);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_200,"Wrong status code.");
        softAssert.assertTrue(appUsers.contains(userNewValues),"User wasn't updated.");
        softAssert.assertFalse(appUsers.contains(userToChange),"User wasn't updated.");
        softAssert.assertAll();
    }

    @SneakyThrows
    @Test(description = "Scenario_2; bug")
    public void putUserIncorrectZipcodeTest() {
        SoftAssert softAssert = new SoftAssert();

        User newUser = User.random();
        UserToUpdate userToUpdate = new UserToUpdate(newUser, userToChange);

        int statusCode = userClient.putUser(userToUpdate);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_424,"Wrong status code.");
        softAssert.assertFalse(appUsers.contains(newUser),"User was updated.");
        softAssert.assertTrue(appUsers.contains(userToChange),
                String.format("User %s was deleted.", userToChange.toString()));
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3; bug")
    public void putUserFieldMissingTest() {
        SoftAssert softAssert = new SoftAssert();

        User userNewValues = User.builder().age(25).zipCode(zipcode).build();
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        int statusCode = userClient.putUser(userToUpdate);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_409,"Wrong status code.");
        softAssert.assertFalse(appUsers.contains(userNewValues),"User was updated.");
        softAssert.assertTrue(appUsers.contains(userToChange),"User was updated.");
        softAssert.assertAll();
    }
}
