package httpTests;

import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;

import java.util.List;

public class UserTest {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    User user;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcode();
        user = userClient.createUserData(zipcode);
    }

    @Test(description = "PostUserZipRemovedTest scenario_1")
    public void postUserZipRemovedTest() {
        SoftAssert softAssert = new SoftAssert();
        int statusCode = userClient.postUser(user);
        List<User> appUsers = userClient.getUsers().getBody();
        List<String> appZipCodes = zipcodeClient.getZipCodes().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_201, "User wasn't added, wrong status code");
        softAssert.assertTrue(appUsers.contains(user), "User wasn't added to the app");
        softAssert.assertFalse(appZipCodes.contains(zipcode), "Zipcode wasn't removed from app zipcodes");
        softAssert.assertAll();
    }

    @Test(description = "PostUserTest scenario_2")
    public void postUserTest() {
        SoftAssert softAssert = new SoftAssert();
        int statusCode = userClient.postUser(user);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_201, "User wasn't added, wrong status code");
        softAssert.assertTrue(appUsers.contains(user), "User wasn't added to the app");
        softAssert.assertAll();
    }

    @Test(description = "PostUserZipIncorrectTest scenario_3")
    public void postUserZipIncorrectTest() {
        SoftAssert softAssert = new SoftAssert();
        String randomZipcode = zipcodeClient.createRandomZipcode();
        User user = userClient.createUserData(randomZipcode);

        int statusCode = userClient.postUser(user);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_424, "User was added, wrong status code.");
        softAssert.assertFalse(appUsers.contains(user), "User was added to the app.");
        softAssert.assertAll();
    }

    @Test(description = "PostUserSameNameSexAsOnAppTest scenario_4")
    public void postUserSameNameSexAsOnAppTest() {
        SoftAssert softAssert = new SoftAssert();
        User user = userClient.getUsers().getBody().get(0);
        User userToPost = new User(50, user.getName(), user.getSex(), zipcode);

        int statusCode = userClient.postUser(userToPost);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_400, "User was added, status isn't 400.");
        softAssert.assertFalse(appUsers.contains(userToPost), "User was added to the app.");
        softAssert.assertAll();
    }
}
