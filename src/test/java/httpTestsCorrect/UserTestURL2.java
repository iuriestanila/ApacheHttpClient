package httpTestsCorrect;

import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;

import java.util.List;

public class UserTestURL2 {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;

    @BeforeMethod
    public void init(){
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcodeURL2();
    }

    @Test(description = "Scenario_1")
    public void postUserZipRemoved(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(28,"Rick","MALE", zipcode );
        int statusCode = userClient.postUserURL2(user);
        List<User> appUsers = userClient.getUsersURL2().getBody();
        List<String> appZipCodes = zipcodeClient.getZipCodesURl2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_201, "User wasn't added, wrong status code");
        softAssert.assertTrue(appUsers.contains(user), "User wasn't added to the app");
        softAssert.assertFalse(appZipCodes.contains(zipcode), "Zipcode wasn't removed from app zipcodes");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_2")
    public void postUser(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(31,"Ingrid","FEMALE",zipcode);
        int statusCode = userClient.postUserURL2(user);
        List<User> appUsers = userClient.getUsersURL2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_201, "User wasn't added, wrong status code");
        softAssert.assertTrue(appUsers.contains(user), "User wasn't added to the app");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void postUserZipIncorrect(){
        SoftAssert softAssert = new SoftAssert();
        User user = new User(50,"Jonas","MALE","FFFFF");
        int statusCode = userClient.postUserURL2(user);
        List<User> appUsers = userClient.getUsersURL2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_424, "User was added, wrong status code.");
        softAssert.assertFalse(appUsers.contains(user), "User was added to the app.");
        softAssert.assertAll();
    }


    @Test(description = "Scenario_4")
    public void postUserSameNameSexAsOnApp(){
        /*
        Given I am authorized user
        When I send POST request to /users endpoint
        And Request body contains user to add with the same name and sex as existing user in the system
        Then I get 400 response code
        And User is not added to application
         */
        SoftAssert softAssert = new SoftAssert();
        User user = new User(51,"Gheorghe","MALE", zipcode);
        int statusCode = userClient.postUserURL2(user);
        List<User> appUsers = userClient.getUsersURL2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_400, "User was added, status isn't 400.");
        softAssert.assertFalse(appUsers.contains(user), "User was added to the app.");
        softAssert.assertAll();
    }
}
