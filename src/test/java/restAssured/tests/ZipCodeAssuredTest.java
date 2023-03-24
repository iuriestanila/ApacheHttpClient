package restAssured.tests;

import apacheHttpClient.client.Client;
import apacheHttpClient.client.ZipCodeClient;
import apacheHttpClient.enums.AccessType;
import apacheHttpClient.utils.Const;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import restAssured.AuthRAssured;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ZipCodeAssuredTest {
    String zipCodeRand1;
    String zipCodeRand2;

    @BeforeMethod
    public void setUp() {
        zipCodeRand1 = RandomStringUtils.randomNumeric(5);
        zipCodeRand2 = RandomStringUtils.randomNumeric(5);
    }

    @Test(description = "Get zipcodes test assured; scenario_1")
    public void getZipCodesAssuredTest() {
        given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when()
                .get(Client.BASE_URL + ZipCodeClient.GET_ZIPCODES_ENDPOINT)
                .then()
                .statusCode(Const.STATUS_200)
                .body("$", hasSize(greaterThan(0)));
    }

    @Test(description = "Post zipcodes test assured; scenario_2")
    public void postZipCodesAssuredTest() {
        List<String> zipCodes = List.of(zipCodeRand1, zipCodeRand2);
        given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(zipCodes)
                .when()
                .post(Client.BASE_URL + ZipCodeClient.POST_ZIPCODES_ENDPOINT)
                .then()
                .statusCode(Const.STATUS_201)
                .body("", hasItems((zipCodes.toArray())));
    }

    @Test(description = "Post duplicate zipcodes in sent list test assured; scenario_3")
    public void postZipCodesInSentListDuplicatesAssuredTest() {
        List<String> duplicates = List.of(zipCodeRand1, zipCodeRand1);

        Response response = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(duplicates)
                .post(Client.BASE_URL + ZipCodeClient.POST_ZIPCODES_ENDPOINT);

        List<String> responseZipCodes = response.jsonPath().getList("$");

        response.then().statusCode(Const.STATUS_201);
        assertThat(Collections.frequency(responseZipCodes, zipCodeRand1), equalTo(1));
    }

    @Test(description = "Post duplicate zipcodes on server test assured; scenario_4")
    public void postZipCodesOnServerDuplicatesAssuredTest() {
        List<String> zipCodes = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.READ))
                .when().get(Client.BASE_URL + ZipCodeClient.GET_ZIPCODES_ENDPOINT)
                .then().extract().body().jsonPath().getList("$");

        String duplicate1 = zipCodes.get(0);
        String duplicate2 = zipCodes.get(0);
        List<String> duplicatesOnServer = List.of(duplicate1, duplicate2);

        Response response = given()
                .header("Authorization", "Bearer " + AuthRAssured.getToken(AccessType.WRITE))
                .header("Content-Type", "application/json")
                .body(duplicatesOnServer)
                .post(Client.BASE_URL + ZipCodeClient.POST_ZIPCODES_ENDPOINT);

        List<String> responseZipCodes = response.jsonPath().getList("$");

        response.then().statusCode(Const.STATUS_201);
        assertThat(Collections.frequency(responseZipCodes, duplicatesOnServer.get(0)), equalTo(1));
        assertThat(Collections.frequency(responseZipCodes, duplicatesOnServer.get(1)), equalTo(1));
    }
}







