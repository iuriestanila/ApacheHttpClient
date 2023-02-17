import client.ZipCodeClient;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pojo.ResponseEntity;
import utils.Const;

import java.util.Arrays;
import java.util.List;

public class ZipCodeTest {

    private ZipCodeClient zipCodeClient;

    @BeforeMethod
    public void setUp() {
        zipCodeClient = new ZipCodeClient();
    }

    @Test(description = "Scenario_1; bug")
    public void getZipCodesTest() {
        final ResponseEntity<List<String>> zipCodes = zipCodeClient.getZipCodes();
        Assert.assertEquals(zipCodes.getStatusCode(), Const.STATUS_200, "Status code is not 200");
        Assert.assertFalse(zipCodes.getBody().isEmpty(),"List of zip codes is empty");
    }

    @Test(description = "Scenario_2")
    public void postZipCodesTest() {
        List<String> zipCodesEnter = zipCodeClient.passZipcodes(Arrays.asList("44444", "55555", "66666"));
        final ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes(zipCodesEnter);
        Assert.assertEquals(zipCodes.getStatusCode(), Const.STATUS_201, "Isn't 201");
        Assert.assertTrue(zipCodes.getBody().containsAll(List.of("44444", "55555", "66666")), "Zip codes aren't added");
    }

    @Test(description = "Scenario_3; bug")
    public void postZipCodesInSentListDuplicatesTest() {
        List<String> zipCodesToEnter = zipCodeClient.passZipcodes(Arrays.asList("MD", "MD", "MD"));
        ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes(zipCodesToEnter);

        int occurrences = zipCodeClient.inSentListDuplicates(zipCodes, "MD");

        Assert.assertEquals(zipCodes.getStatusCode(), Const.STATUS_201, "Isn't 201");
        Assert.assertEquals(occurrences, 1, "Duplicates were created");
    }

    @Test(description = "Scenario_4; bug")
    public void postZipCodesOnServerDuplicatesTest() {
        List<String> zipCodesToEnter = zipCodeClient.passZipcodes(Arrays.asList("a", "b", "c"));
        String[] zipArray = zipCodesToEnter.toArray(new String[0]);

        ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes(zipCodesToEnter);

        int result = zipCodeClient.onServerDuplicates(zipCodes, zipArray);

        Assert.assertEquals(zipCodes.getStatusCode(), Const.STATUS_201, "Isn't 201");
        Assert.assertEquals(result, 3, "Duplicates were created");
    }
}
