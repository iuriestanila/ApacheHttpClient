package httpTestsCorrect;

import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import apacheHttpClient.pojo.ResponseEntity;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;

import java.util.List;

public class FilterUserURL2Test {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcodeURL2();
    }

    @Test(description = "Scenario_1")
    public void getUserTest() {
        SoftAssert softAssert = new SoftAssert();
        final ResponseEntity<List<User>> responseEntity = userClient.getUsersURL2();
        softAssert.assertEquals(responseEntity.getStatusCode(), Const.STATUS_200, "Wrong status, should be 200");
        softAssert.assertNotNull(responseEntity.getBody(), "User wasn't added to the app");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_2")
    public void getUserOlderThanTest() {
        SoftAssert softAssert = new SoftAssert();
        ResponseEntity<List<User>> response = userClient.getUsersParamURL2("olderThan", "30");

        softAssert.assertEquals(response.getStatusCode(), Const.STATUS_200, "Wrong status code");
        softAssert.assertTrue(response.getBody().stream().allMatch(user -> user.getAge() > 30), "User is younger than 30");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void getUserYoungerThanTest() {
        SoftAssert softAssert = new SoftAssert();
        ResponseEntity<List<User>> response = userClient.getUsersParamURL2("youngerThan", "25");

        softAssert.assertEquals(response.getStatusCode(), Const.STATUS_200, "Wrong status code");
        softAssert.assertTrue(response.getBody().stream().allMatch(user -> user.getAge() < 25), "User is older than 25");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_4")
    public void getUserSexParameterTest() {
        SoftAssert softAssert = new SoftAssert();
        ResponseEntity<List<User>> response = userClient.getUsersParamURL2("sex", "MALE");

        softAssert.assertEquals(response.getStatusCode(), Const.STATUS_200, "Wrong status code");
        softAssert.assertTrue(response.getBody().stream().allMatch(user -> user.getSex().equals("MALE")), "User isn't male");
        softAssert.assertAll();
    }
}

