import client.UserClient;
import client.ZipCodeClient;
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
    private String zipcodeRandom;
    private User postUser;
    private User dataUser;

    @BeforeMethod
    public void init() {
        userClient = new UserClient();
        zipcodeClient = new ZipCodeClient();
        zipcodeRandom = zipcodeClient.createAvailableZipcode();
        postUser = userClient.createAvailableUser(zipcodeRandom);
        dataUser = userClient.createRandomUser(zipcodeRandom);
    }

    @Test(description = "Scenario_1")
    public void patchUserTest() {
        SoftAssert softAssert = new SoftAssert();

        User userToChange = new User(50, "George","MALE", "63912");
        User userNewValues = new User(100,"George","MALE","78847");
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        int statusCode = userClient.patchUser(userToUpdate);
        List<User> appUsers = userClient.getUser().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_200,"Wrong status code.");
        softAssert.assertTrue(appUsers.contains(userNewValues),"User wasn't updated.");
        softAssert.assertAll();
    }

    @Test(description = "Scenario_2; bug")
    public void putUserIncorrectZipcodeTest() {
        SoftAssert softAssert = new SoftAssert();

        User userToChange = new User(postUser.getAge(), postUser.getName(), postUser.getSex(), postUser.getZipCode());
        User userNewValues = new User(dataUser.getAge(),dataUser.getName(),dataUser.getSex(), dataUser.getZipCode());
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        int statusCode = userClient.putUser(userToUpdate);
        List<User> appUsers = userClient.getUser().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_424,"Wrong status code.");
        softAssert.assertFalse(appUsers.contains(userNewValues),"User was updated.");
        softAssert.assertAll();

    }

    @Test(description = "Scenario_3")
    public void putUserFieldMissingTest() {
        SoftAssert softAssert = new SoftAssert();

        User userToChange = new User(postUser.getAge(), postUser.getName(), postUser.getSex(), postUser.getZipCode());
        User userNewValues = new User(dataUser.getAge(),dataUser.getSex(), dataUser.getZipCode());
        UserToUpdate userToUpdate = new UserToUpdate(userNewValues, userToChange);

        int statusCode = userClient.putUser(userToUpdate);
        List<User> appUsers = userClient.getUser().getBody();

        softAssert.assertEquals(statusCode, Const.STATUS_409,"Wrong status code.");
        softAssert.assertFalse(appUsers.contains(userNewValues),"User was updated.");
        softAssert.assertAll();
    }
}
