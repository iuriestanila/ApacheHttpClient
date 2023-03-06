import client.UserClient;
import client.ZipCodeClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pojo.User;
import utils.Const;

import java.util.List;

public class DeleteUserTest {
    private UserClient userClient;
    private ZipCodeClient zipcodeClient;
    private String zipcode;
    private User userToDelete;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcode = zipcodeClient.createAvailableZipcode();
        userToDelete = userClient.createAvailableUser(zipcode);
    }

    @Test(description = "Scenario_1")
    public void deleteUserTest() {
        SoftAssert softAssert = new SoftAssert();

        int statusCode = userClient.deleteUser(userToDelete);
        List<User> appUsers = userClient.getUsers().getBody();
        List<String> zipCodesApp = zipcodeClient.getZipCodes().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_204,"Wrong status code.");
        softAssert.assertFalse(appUsers.contains(userToDelete),"User wasn't deleted.");
        softAssert.assertTrue(zipCodesApp.contains(userToDelete.getZipCode()),
                "Zip code wasn't returned in list of available zip codes");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_2; bug")
    public void fillRequiredFieldsDeleteTest() {
        SoftAssert softAssert = new SoftAssert();
        User userToDeleteRequiredFields = User.builder().name(userToDelete.getName()).sex(userToDelete.getSex()).build();
        int statusCode = userClient.deleteUser(userToDeleteRequiredFields);
        List<User> appUsers = userClient.getUsers().getBody();
        List<String> zipCodesApp = zipcodeClient.getZipCodes().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_204,"Wrong status code.");
        softAssert.assertFalse(appUsers.contains(userToDelete),"User wasn't deleted.");
        softAssert.assertTrue(zipCodesApp.contains(userToDelete.getZipCode()),
                "Zip code wasn't returned in list of available zip codes");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_3")
    public void requiredFieldMissDeleteTest() {
        SoftAssert softAssert = new SoftAssert();
        User userToDeleteRequiredFields = User.builder().name(userToDelete.getName()).build();
        int statusCode = userClient.deleteUser(userToDeleteRequiredFields);
        List<User> appUsers = userClient.getUsers().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_409,"Wrong status code.");
        softAssert.assertTrue(appUsers.contains(userToDelete),"User was deleted.");
        softAssert.assertAll();
    }
}
