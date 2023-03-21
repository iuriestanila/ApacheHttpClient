package httpTests;

import apacheHttpClient.client.ZipCodeClient;
import io.qameta.allure.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import apacheHttpClient.pojo.ResponseEntity;
import apacheHttpClient.utils.Const;

import java.util.Collections;
import java.util.List;

public class ZipCodeTest {

    private ZipCodeClient zipCodeClient;
    String zipCodeRand1;
    String zipCodeRand2;

    @BeforeMethod
    public void setUp() {
        zipCodeClient = new ZipCodeClient();
        zipCodeRand1 = RandomStringUtils.randomNumeric(5);
        zipCodeRand2 = RandomStringUtils.randomNumeric(5);
    }

    @Issue("GML-20.1")
    @Test(description = "Scenario_1")
    public void getZipCodesTest() {
        final ResponseEntity<List<String>> zipCodes = zipCodeClient.getZipCodes();
        Assert.assertEquals(zipCodes.getStatusCode(), Const.STATUS_200, "Status code is not 200");
        Assert.assertFalse(zipCodes.getBody().isEmpty(),"List of zip codes is empty");
    }

    @Test(description = "Scenario_2")
    public void postZipCodesTest() {
        final ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes(List.of(zipCodeRand1, zipCodeRand2));

        Assert.assertEquals(zipCodes.getStatusCode(),Const.STATUS_201,"Isn't 201");
        Assert.assertTrue(zipCodes.getBody().contains(zipCodeRand1),"Zip code wasn't added");
        Assert.assertTrue(zipCodes.getBody().contains(zipCodeRand2),"Zip code wasn't added");
    }

    @Issue("GML-20.2")
    @Test(description = "Scenario_3")
    public void postZipCodesInSentListDuplicatesTest() {
           ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes(List.of(zipCodeRand1, zipCodeRand1));
           Assert.assertEquals(zipCodes.getStatusCode(),Const.STATUS_201,"Isn't 201");
           Assert.assertEquals(Collections.frequency(zipCodes.getBody(), zipCodeRand1),
                   1, "Duplicates were created");
    }

    @Issue("GML-20.3")
    @Test(description = "Scenario_4")
    public void postZipCodesOnServerDuplicatesTest() {
        List zipCodesOnServer = List.of("qqqqq", "22222");
        ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes(zipCodesOnServer);

        Assert.assertEquals(zipCodes.getStatusCode(),Const.STATUS_201,"Isn't 201");
        Assert.assertEquals(Collections.frequency(zipCodes.getBody(), zipCodesOnServer.get(0)),
                1, "Duplicate was created");
        Assert.assertEquals(Collections.frequency(zipCodes.getBody(), zipCodesOnServer.get(1)),
                1, "Duplicate was created");
    }
}
