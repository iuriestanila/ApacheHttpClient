package httpTestsCorrect;

import apacheHttpClient.client.UserClient;
import apacheHttpClient.client.ZipCodeClient;
import io.qameta.allure.Issue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import apacheHttpClient.pojo.User;
import apacheHttpClient.utils.Const;

import java.util.List;

public class DeleteUserURL2Test {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToDelete;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcodeURL2();
        userToDelete = userClient.createAvailableUserURL2(zipcode);
    }

    @Test(description = "Scenario_1")
    public void deleteUserTest() {
        SoftAssert softAssert = new SoftAssert();

        int statusCode = userClient.deleteUserURL2(userToDelete);
        List<User> users = userClient.getUsersURL2().getBody();
        List<String> zipCodes = zipcodeClient.getZipCodesURl2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_204,"Wrong status code.");
        softAssert.assertFalse(users.contains(userToDelete),"User wasn't deleted.");
        softAssert.assertTrue(zipCodes.contains(zipcode),
                "Zip code wasn't returned in list of available zip codes");
        softAssert.assertAll();
    }

    @Issue("GML-60.1")
    @Test(description = "Scenario_2")
    public void fillRequiredFieldsDeleteTest() {
        SoftAssert softAssert = new SoftAssert();
        User userToDeleteRequiredFields = User.builder().name(userToDelete.getName()).sex(userToDelete.getSex()).build();
        int statusCode = userClient.deleteUserURL2(userToDeleteRequiredFields);
        List<User> users = userClient.getUsersURL2().getBody();
        List<String> zipCodes = zipcodeClient.getZipCodesURl2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_204,"Wrong status code.");
        softAssert.assertFalse(users.contains(userToDelete),"User wasn't deleted.");
        softAssert.assertTrue(zipCodes.contains(userToDelete.getZipCode()),
                "Zip code wasn't returned in list of available zip codes");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void requiredFieldMissDeleteTest() {
        SoftAssert softAssert = new SoftAssert();
        User userToDeleteRequiredFields = User.builder().name(userToDelete.getName()).build();
        int statusCode = userClient.deleteUserURL2(userToDeleteRequiredFields);
        List<User> users = userClient.getUsersURL2().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_409,"Wrong status code.");
        softAssert.assertTrue(users.contains(userToDelete),"User was deleted.");
        softAssert.assertAll();
    }
}
