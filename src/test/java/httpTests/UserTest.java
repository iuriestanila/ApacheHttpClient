package httpTests;

import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import io.qameta.allure.Issue;
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

    @BeforeMethod
    public void init(){
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcode();
    }

    @Test(description = "Scenario_1")
    public void postUserZipRemoved(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(28,"Rick","MALE", zipcode );
        int statusCode = userClient.postUser(user);
        List<User> appUsers = userClient.getUsers().getBody();
        List<String> appZipCodes = zipcodeClient.getZipCodes().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_201, "User wasn't added, wrong status code");
        softAssert.assertTrue(appUsers.contains(user), "User wasn't added to the app");
        softAssert.assertFalse(appZipCodes.contains(zipcode), "Zipcode wasn't removed from app zipcodes");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_2")
    public void postUser(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(31,"Ingrid","FEMALE",zipcode);
        int statusCode = userClient.postUser(user);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_201, "User wasn't added, wrong status code");
        softAssert.assertTrue(appUsers.contains(user), "User wasn't added to the app");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void postUserZipIncorrect(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(50,"Jonas","MALE","FFFFF");
        int statusCode = userClient.postUser(user);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_424, "User was added, wrong status code.");
        softAssert.assertFalse(appUsers.contains(user), "User was added to the app.");
        softAssert.assertAll();
    }

    @Issue("GML-30.1")
    @Test(description = "Scenario_4")
    public void postUserSameNameSexAsOnApp(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(50,"George","MALE", zipcode);
        int statusCode = userClient.postUser(user);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_400, "User was added, status isn't 400.");
        softAssert.assertFalse(appUsers.contains(user), "User was added to the app.");
        softAssert.assertAll();
    }
}
