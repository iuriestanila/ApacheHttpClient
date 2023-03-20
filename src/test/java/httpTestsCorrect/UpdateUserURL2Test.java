package httpTestsCorrect;

import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import io.qameta.allure.Issue;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import apacheHttpClient.pojo.User;
import apacheHttpClient.pojo.UserToUpdate;
import apacheHttpClient.utils.Const;

import java.util.List;

public class UpdateUserURL2Test {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToChange;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcodeURL2();
        userToChange = userClient.createAvailableUserURL2(zipcode);
    }

    @Test(description = "Scenario_1")
    public void patchUserTest() {
        SoftAssert softAssert = new SoftAssert();

        User userNewValues = User.random(zipcode);
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        int statusCode = userClient.patchUserURL2(userToUpdate);
        List<User> appUsers = userClient.getUsersURL2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_200,"Wrong status code.");
        softAssert.assertTrue(appUsers.contains(userNewValues),"User wasn't updated.");
        softAssert.assertFalse(appUsers.contains(userToChange),"User wasn't updated.");
        softAssert.assertAll();
    }

    @Issue("GML-50.1")
    @SneakyThrows
    @Test(description = "Scenario_2")
    public void putUserIncorrectZipcodeTest() {
        SoftAssert softAssert = new SoftAssert();

        User newUser = User.random();
        UserToUpdate userToUpdate = new UserToUpdate(newUser, userToChange);

        int statusCode = userClient.putUserURL2(userToUpdate);
        List<User> appUsers = userClient.getUsersURL2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_424,"Wrong status code.");
        softAssert.assertFalse(appUsers.contains(newUser),"User was updated.");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void putUserFieldMissingTest() {
        SoftAssert softAssert = new SoftAssert();

        User userNewValues = User.builder().age(25).zipCode(zipcode).build();
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        int statusCode = userClient.putUserURL2(userToUpdate);
        List<User> appUsers = userClient.getUsersURL2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_409,"Wrong status code.");
        softAssert.assertFalse(appUsers.contains(userNewValues),"User was updated.");
        softAssert.assertAll();
    }
}
