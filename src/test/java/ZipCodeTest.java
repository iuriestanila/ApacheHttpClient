import client.ZipCodeClient;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pojo.ResponseEntity;
import utils.Const;

import java.util.HashSet;
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
        final ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes();
        Assert.assertEquals(zipCodes.getStatusCode(), Const.STATUS_201, "Isn't 201");
        Assert.assertTrue(zipCodes.getBody().containsAll(List.of("44444", "55555", "66666")), "Zip codes aren't added");
    }

    @Test(description = "Scenario_3; bug")
    public void postZipCodesDuplicationTest() {
        final ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes();
        boolean compare = zipCodes.getBody().size() == new HashSet<>(zipCodes.getBody()).size();

        Assert.assertEquals(zipCodes.getStatusCode(), Const.STATUS_201, "Isn't 201");
        Assert.assertTrue(zipCodes.getBody().containsAll(List.of("44444", "55555", "66666")), "Zip codes aren't added");
        Assert.assertTrue(compare, "Duplicate zip codes found");
    }

    @Test(description = "Scenario_4; bug")
    public void postZipCodesDuplicationTest2() {
        final ResponseEntity<List<String>> zipCodes = zipCodeClient.postZipCodes();
        boolean compare = zipCodes.getBody().size() == new HashSet<>(zipCodes.getBody()).size();

        Assert.assertEquals(zipCodes.getStatusCode(), Const.STATUS_201, "Isn't 201");
        Assert.assertTrue(zipCodes.getBody().containsAll(List.of("44444", "55555", "66666")), "Zip codes aren't added");
        Assert.assertTrue(compare, "Duplicate zip codes found");
    }
}
